package com.open.tool.revolut.model.rest.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRestReq {

    @NotBlank(message = "Name (name) cannot be null")
    private String name;

}
