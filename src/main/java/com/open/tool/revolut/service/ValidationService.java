package com.open.tool.revolut.service;

import com.open.tool.revolut.exception.ValidationFailException;
import spark.Request;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationService<T> {

    private Validator validator;

    public ValidationService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public Set<ConstraintViolation<T>> validate(T object) {
        return validator.validate(object);
    }

    public void validateAsString(T object) throws ValidationFailException {
        String errors = validate(object).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("\n"));
        if(!errors.isEmpty()) {
            throw new ValidationFailException(errors, 400);
        }
    }


    public void checkBody(Request req) throws ValidationFailException {
        if (req.body() == null) {
            throw new ValidationFailException("Empty body", 400);
        }
    }

}
