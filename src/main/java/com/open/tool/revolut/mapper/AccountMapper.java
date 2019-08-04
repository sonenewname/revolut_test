package com.open.tool.revolut.mapper;

import com.open.tool.revolut.model.db.Account;
import com.open.tool.revolut.model.rest.req.AccountRestReq;
import com.open.tool.revolut.model.rest.res.AccountRestRes;

import java.math.BigDecimal;

public class AccountMapper {

    public static Account fromRestToDb(AccountRestReq accountRestReq) {
        return Account.builder()
                .balance(BigDecimal.ZERO)
                .name(accountRestReq.getName())
                .build();
    }

    public static AccountRestRes fromDbToRest(Account account) {
        return AccountRestRes.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .name(account.getName())
                .build();
    }
}
