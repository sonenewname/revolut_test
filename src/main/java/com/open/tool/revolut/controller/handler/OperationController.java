package com.open.tool.revolut.controller.handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.open.tool.revolut.controller.Controller;
import com.open.tool.revolut.exception.BaseException;
import com.open.tool.revolut.exception.ValidationFailException;
import com.open.tool.revolut.mapper.OperationMapper;
import com.open.tool.revolut.model.rest.req.OperationRestReq;
import com.open.tool.revolut.service.OperationService;
import com.open.tool.revolut.service.ValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import static spark.Spark.path;
import static spark.Spark.post;

@Singleton
public class OperationController extends Controller<OperationRestReq> {

    private static Logger log = LoggerFactory.getLogger(OperationController.class);
    private final OperationService operationService;

    @Inject
    public OperationController(final OperationService operationService,
                               final ValidationService<OperationRestReq> validationService) {
        super(validationService, OperationRestReq.class);
        this.operationService = operationService;
    }

    @Override
    public void init() {
        log.info("Initializing OperationController");

        path("/operations", () -> {
            post("/deposit", (req, res) -> {
                try {
                    OperationRestReq operationRestReq = parseAndValidateRequestBody(req);

                    return json(res,
                            OperationMapper.fromDbToRest(
                                    operationService.doDeposit(operationRestReq)
                            )
                    );
                } catch (BaseException ex) {
                    return error(res, ex.getStatus(), ex.getMessage());
                } catch (JsonSyntaxException ex) {
                    log.warn("Can't transform request json to OperationRestReq: body={}", req.body());
                    return error(res, 400, "Wrong json format");
                }

            }, gson::toJson);
            post("", (req, res) -> {

                try {
                    OperationRestReq operationRestReq = parseAndValidateRequestBody(req);
                    return json(res,
                            OperationMapper.fromDbToRest(
                                    operationService.doOperation(operationRestReq)
                            )
                    );
                } catch (BaseException ex) {
                    return error(res, ex.getStatus(), ex.getMessage());
                } catch (JsonSyntaxException ex) {
                    log.warn("Can't transform request json to OperationRestReq: body={}", req.body());
                    return error(res, 400, "Wrong json format");
                }

            }, gson::toJson);
        });
    }


}
