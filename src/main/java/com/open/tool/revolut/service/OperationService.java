package com.open.tool.revolut.service;


import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.open.tool.revolut.exception.BackendException;
import com.open.tool.revolut.exception.BaseException;
import com.open.tool.revolut.exception.InsufficientFundsException;
import com.open.tool.revolut.exception.NotFoundException;
import com.open.tool.revolut.model.db.Account;
import com.open.tool.revolut.model.db.Operation;
import com.open.tool.revolut.model.rest.req.OperationRestReq;
import com.open.tool.revolut.repository.AccountRepository;
import com.open.tool.revolut.repository.OperationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@Singleton
public class OperationService {

    private static Logger log = LoggerFactory.getLogger(OperationService.class);

    private final AccountRepository accountRepository;
    private final OperationRepository operationRepository;

    @Inject
    public OperationService(final AccountRepository accountRepository,
                            final OperationRepository operationRepository) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
    }

    public Operation doDeposit(OperationRestReq req) throws BaseException {
        Operation operation = new Operation();

        Account to = accountRepository.findAccountById(req.getTo());
        if (to == null) {
            throw new NotFoundException("Recipient account with id : " + req.getTo() + " not found", 404);
        }

        operation.setTo(to);

        makeDeposit(to, req.getAmount());
        operation.setAmount(req.getAmount());

        operationRepository.saveOperation(operation);

        return operation;
    }


    public Operation doOperation(OperationRestReq req) throws BaseException {

        Operation operation = new Operation();

        if(req.getFrom().equals(req.getTo())) {
            throw new BackendException("Sender (from) and recipient (to) should be different", 400);
        }

        Account from = accountRepository.findAccountById(req.getFrom());
        if (from == null) {
            throw new NotFoundException("Sender account with id: " + req.getFrom() + " not found", 404);
        }

        operation.setFrom(from);

        Account to = accountRepository.findAccountById(req.getTo());
        if (to == null) {
            throw new NotFoundException("Recipient account with id: " + req.getTo() + " not found", 404);
        }

        operation.setTo(to);

        makeTransaction(from, to, req.getAmount());

        operation.setAmount(req.getAmount());

        operationRepository.saveOperation(operation);

        return operation;
    }

    private synchronized void makeTransaction(Account from, Account to, BigDecimal amount)
            throws BaseException {
        log.info("Making transaction between Account(id={}, name={}) and Account(id={}, name={})",
                from.getId(), from.getName(),
                to.getId(), to.getName()
        );

        if (from.getBalance().compareTo(amount) >= 0) {
            from.setBalance(from.getBalance().subtract(amount));
        } else {
            throw new InsufficientFundsException("Account don't have enough money", 400);
        }
        to.setBalance(to.getBalance().add(amount));
        accountRepository.update(from);
        accountRepository.update(to);
    }

    private synchronized void makeDeposit(Account to, BigDecimal amount) throws BaseException {
        to.setBalance(to.getBalance().add(amount));
        accountRepository.update(to);
    }
}
