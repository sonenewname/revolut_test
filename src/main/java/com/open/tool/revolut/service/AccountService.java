package com.open.tool.revolut.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.open.tool.revolut.exception.BackendException;
import com.open.tool.revolut.exception.BaseException;
import com.open.tool.revolut.exception.NotFoundException;
import com.open.tool.revolut.mapper.AccountMapper;
import com.open.tool.revolut.model.db.Account;
import com.open.tool.revolut.model.rest.req.AccountRestReq;
import com.open.tool.revolut.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AccountService {

    private static Logger log = LoggerFactory.getLogger(AccountService.class);
    private final AccountRepository accountRepository;

    @Inject
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(AccountRestReq accountRestReq) throws BaseException {
        Account accountByNameAndCurrency = accountRepository.findAccountByName(accountRestReq.getName());

        if (accountByNameAndCurrency != null) {
            log.warn("Account exists: id={}, name={}",
                    accountByNameAndCurrency.getId(),
                    accountRestReq.getName());


            throw new BackendException("Account with name=" + accountRestReq.getName() + " exist", 400);
        }
        return accountRepository.createAccount(AccountMapper.fromRestToDb(accountRestReq));
    }

    public Account findByIdFull(String accountId) throws BaseException {
        Account accountByIdFull = accountRepository.findAccountByIdFull(getIdAsLong(accountId));
        if (accountByIdFull == null) {
            throw new NotFoundException("Account not found", 404);
        }
        return accountByIdFull;
    }

    public Account findById(String accountId) throws BaseException {
        Long id = getIdAsLong(accountId);
        return findById(id);
    }

    private Long getIdAsLong(String accountId) throws BackendException {
        Long id;
        try {
            id = Long.parseLong(accountId);
        } catch (NumberFormatException ex) {
            throw new BackendException("Account id should be numeric", 400);
        }
        return id;
    }

    public Account findById(Long id) throws BaseException {
        Account accountById = accountRepository.findAccountById(id);
        if (accountById == null) {
            throw new NotFoundException("Account not found", 404);
        }
        return accountById;
    }

}
