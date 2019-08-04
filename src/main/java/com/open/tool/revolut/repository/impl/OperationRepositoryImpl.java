package com.open.tool.revolut.repository.impl;

import com.google.inject.Singleton;
import com.open.tool.revolut.exception.BackendException;
import com.open.tool.revolut.model.db.Operation;
import com.open.tool.revolut.repository.OperationRepository;

@Singleton
public class OperationRepositoryImpl extends AbstractRepository<Operation> implements OperationRepository {


    /**
     * {@inheritDoc}
     */
    public Operation saveOperation(Operation operation) throws BackendException {
        if(operation == null) {
            throw new IllegalArgumentException("'Operation' must be not null");
        }

        return doInTransaction(() -> {
            session().save(operation);
            return operation;
        });
    }

}
