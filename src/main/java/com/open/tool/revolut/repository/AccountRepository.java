package com.open.tool.revolut.repository;

import com.open.tool.revolut.exception.BaseException;
import com.open.tool.revolut.model.db.Account;

import javax.validation.constraints.NotNull;


public interface AccountRepository {

    /**
     * @param account - is object for save
     * @return id of new account
     */
    Account createAccount(Account account) throws BaseException;


    /**
     * @param id of account
     * @return account or null if not found
     */
    Account findAccountById(@NotNull Long id) throws BaseException;

    /**
     * Find account and all lazy relations
     * @param id of account
     * @return account or null if not found
     */
    Account findAccountByIdFull(@NotNull Long id) throws BaseException;

    void update(Account account) throws BaseException;

    Account findAccountByName(String name) throws BaseException;
}
