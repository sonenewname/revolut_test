package com.open.tool.revolut.repository;

import com.open.tool.revolut.exception.BaseException;
import com.open.tool.revolut.model.db.Operation;

public interface OperationRepository {


    /**
     * @param operation - is object for save
     * @return id of new operation
     */
    Operation saveOperation(Operation operation) throws BaseException;

}
