package com.open.tool.revolut.mapper;

import com.open.tool.revolut.model.db.Operation;
import com.open.tool.revolut.model.rest.req.OperationRestReq;
import com.open.tool.revolut.model.rest.res.OperationRestRes;

import java.util.List;
import java.util.stream.Collectors;

public class OperationMapper {

    public static OperationRestRes fromDbToRest(Operation operation) {
        OperationRestRes operationRestRes = OperationRestRes.builder()
                .amount(operation.getAmount())
                .to(operation.getTo().getId())
                .build();
        if(operation.getFrom() != null) {
            operationRestRes.setFrom(operation.getFrom().getId());
        }
        return operationRestRes;
    }

    public static List<OperationRestRes> fromDbToRestList(List<Operation> operations) {
        return operations.stream()
                .map(OperationMapper::fromDbToRest)
                .collect(Collectors.toList());
    }


}
