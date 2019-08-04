package com.open.tool.revolut.service;

import com.open.tool.revolut.exception.BaseException;
import com.open.tool.revolut.model.db.Account;
import com.open.tool.revolut.model.db.Operation;
import com.open.tool.revolut.model.rest.req.OperationRestReq;
import com.open.tool.revolut.repository.AccountRepository;
import com.open.tool.revolut.repository.OperationRepository;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OperationServiceTest {

    private OperationService operationService;
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;

    @Before
    public void setUp() throws Exception {
        this.accountRepository = mock(AccountRepository.class);
        this.operationRepository = mock(OperationRepository.class);
        this.operationService = new OperationService(accountRepository, operationRepository);
    }

    @Test
    public void doDeposit() throws BaseException {
        Account accountFromDB = Account.builder()
                .name("test1")
                .balance(BigDecimal.ZERO)
                .id(1L)
                .build();
        when(accountRepository.findAccountById(anyLong())).thenReturn(accountFromDB);
        when(operationRepository.saveOperation(any(Operation.class))).thenReturn(anyObject());

        Operation operation = operationService.doDeposit(OperationRestReq.builder()
                .amount(BigDecimal.TEN)
                .from(1L)
                .to(2L)
                .build());

        assertNull(operation.getFrom());
        assertEquals(BigDecimal.TEN, operation.getAmount());
        assertEquals(accountFromDB, operation.getTo());
    }

    @Test
    public void doOperation() throws BaseException {
        Account firstAccountFromDB = Account.builder()
                .name("test1")
                .balance(BigDecimal.valueOf(100))
                .id(1L)
                .build();
        when(accountRepository.findAccountById(1L)).thenReturn(firstAccountFromDB);

        Account secondAccountFromDB = Account.builder()
                .name("test1")
                .balance(BigDecimal.ZERO)
                .id(1L)
                .build();
        when(accountRepository.findAccountById(2L)).thenReturn(secondAccountFromDB);
        when(operationRepository.saveOperation(any(Operation.class))).thenReturn(anyObject());

        double amount = 12.32;
        Operation operation = operationService.doOperation(OperationRestReq.builder()
                .from(1L)
                .to(2L)
                .amount(BigDecimal.valueOf(amount))
                .build());


        assertEquals(BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(amount)), firstAccountFromDB.getBalance());
        assertEquals(BigDecimal.valueOf(amount), secondAccountFromDB.getBalance());
        assertEquals(BigDecimal.valueOf(amount), operation.getAmount());
    }
}