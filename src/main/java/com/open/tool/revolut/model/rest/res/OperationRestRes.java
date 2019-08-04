package com.open.tool.revolut.model.rest.res;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationRestRes {

    private Long from;
    private Long to;
    private BigDecimal amount;
}
