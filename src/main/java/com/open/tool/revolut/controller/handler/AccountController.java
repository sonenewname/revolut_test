package com.open.tool.revolut.controller.handler;

import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.open.tool.revolut.controller.Controller;
import com.open.tool.revolut.exception.BaseException;
import com.open.tool.revolut.exception.ValidationFailException;
import com.open.tool.revolut.mapper.AccountMapper;
import com.open.tool.revolut.mapper.OperationMapper;
import com.open.tool.revolut.model.db.Account;
import com.open.tool.revolut.model.db.Operation;
import com.open.tool.revolut.model.rest.req.AccountRestReq;
import com.open.tool.revolut.service.AccountService;
import com.open.tool.revolut.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static spark.Spark.*;


@Singleton
public class AccountController extends Controller<AccountRestReq> {

    private static Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    @Inject
    public AccountController(final AccountService accountService,
                             final ValidationService<AccountRestReq> validationService) {
        super(validationService, AccountRestReq.class);
        this.accountService = accountService;
    }


    @Override
    public void init() {
        log.info("Initializing AccountController");
        path("/accounts", () -> {
            get("/:id", (req, res) -> {
                String accountId = req.params(":id");

                log.info("Searching account by id: {}", accountId);

                Account accountById;
                try {
                    accountById = accountService.findById(accountId);
                } catch (BaseException ex) {
                    log.warn("Account not found, because: {}", ex.getMessage());
                    return error(res, ex.getStatus(), ex.getMessage());
                }
                log.info("Found account: id={}, name={}", accountById.getId(), accountById.getName());


                return json(res, AccountMapper.fromDbToRest(accountById));
            }, gson::toJson);

            get("/:id/operations", (req, res) -> {
                String accountId = req.params(":id");

                log.info("Searching all operations by account id: {}", accountId);

                Account accountById;
                try {
                    accountById = accountService.findByIdFull(accountId);
                } catch (BaseException ex) {
                    log.warn("Operations not found, because: {}", ex.getMessage());
                    return error(res, ex.getStatus(), ex.getMessage());
                }

                List<Operation> operations = Stream.concat(
                        accountById.getOutOperations().stream(),
                        accountById.getInOperations().stream()
                ).collect(Collectors.toList());

                log.info("Found {} operations", operations.size());

                return json(res, OperationMapper.fromDbToRestList(operations));


            }, gson::toJson);
            post("", (req, res) -> {
                try {

                    log.info("Adding new account");

                    AccountRestReq accountRequest = parseAndValidateRequestBody(req);

                    log.info("Validation was passed");

                    Account account;
                    try {
                        account = accountService.createAccount(accountRequest);
                    } catch (BaseException ex) {
                        log.warn("Error with creating account, {}", ex.getMessage());
                        return error(res, ex.getStatus(), ex.getMessage());
                    }
                    return json(res, AccountMapper.fromDbToRest(account));

                } catch (ValidationFailException ex) {
                    log.warn("Request validation was fail: message(s)={}, body={}",
                            ex.getMessage(), req.body());
                    return error(res, ex.getStatus(), ex.getMessage());

                } catch (JsonSyntaxException ex) {
                    log.warn("Can't transform request json to AccountRestReq: body={}", req.body());
                    return error(res, 400, "Wrong json format");
                }

            }, gson::toJson);
        });
    }
}
