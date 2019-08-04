package com.open.tool.revolut.model.rest.res;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@Builder
public class AccountRestRes {

    private Long id;

    private BigDecimal balance;

    private String name;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountRestRes)) return false;
        AccountRestRes that = (AccountRestRes) o;
        return Objects.equals(getId(), that.getId()) &&
                getBalance().compareTo(that.getBalance()) == 0 &&
                getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getBalance(), getName());
    }
}
