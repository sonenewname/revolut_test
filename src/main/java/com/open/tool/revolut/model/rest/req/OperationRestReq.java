package com.open.tool.revolut.model.rest.req;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationRestReq {

    private Long from;

    @NotNull(message = "Recipient (to) must be provided")
    private Long to;

    @NotNull(message = "Amount (amount) for transfer must be provided")
    @Min(value = 0, message = "Amount must be > 0")
    private BigDecimal amount;


}
