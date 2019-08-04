package com.open.tool.revolut.controller;

import com.google.gson.Gson;
import com.open.tool.revolut.exception.ValidationFailException;
import com.open.tool.revolut.model.rest.req.OperationRestReq;
import com.open.tool.revolut.model.rest.res.ErrorRes;
import com.open.tool.revolut.service.ValidationService;
import spark.Request;
import spark.Response;

public abstract class Controller<T> {

    protected Gson gson = new Gson();
    protected final ValidationService<T> validationService;
    private final Class<T> type;

    protected Controller(ValidationService<T> validationService, Class<T> type) {
        this.validationService = validationService;
        this.type = type;
    }


    public abstract void init();

    protected Object error(Response res, int status, String message) {
        res.status(status);
        return json(res, new ErrorRes(message));
    }

    protected Object json(Response res, Object responseObject) {
        res.type("application/json");
        return responseObject;
    }


    protected T parseAndValidateRequestBody(Request req) throws ValidationFailException {
        validationService.checkBody(req);
        T obj = new Gson().fromJson(req.body(), type);
        validationService.validateAsString(obj);
        return obj;
    }

}
