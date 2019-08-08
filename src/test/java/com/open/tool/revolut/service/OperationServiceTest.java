package com.open.tool.revolut.service;

import com.open.tool.revolut.exception.BaseException;
import com.open.tool.revolut.model.db.Account;
import com.open.tool.revolut.model.db.Operation;
import com.open.tool.revolut.model.rest.req.OperationRestReq;
import com.open.tool.revolut.repository.AccountRepository;
import com.open.tool.revolut.repository.OperationRepository;
import com.open.tool.revolut.service.OperationService;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    @Test
    public void multithreadingOperationTest() throws BaseException, InterruptedException, ExecutionException, TimeoutException {
        Account firstAccountFromDB = Account.builder()
                .name("test1")
                .balance(BigDecimal.valueOf(15000))
                .id(1L)
                .build();
        when(accountRepository.findAccountById(1L)).thenReturn(firstAccountFromDB);

        Account secondAccountFromDB = Account.builder()
                .name("test2")
                .balance(BigDecimal.valueOf(15000))
                .id(2L)
                .build();
        when(accountRepository.findAccountById(2L)).thenReturn(secondAccountFromDB);
        when(operationRepository.saveOperation(any(Operation.class))).thenReturn(anyObject());

        ExecutorService executor = Executors.newFixedThreadPool(1000);
        List<Callable<Operation>> callables = new ArrayList<>();
        Random random = new Random();
        int total = 0;
        for (int i = 0; i < 100; i++) {
            int randomValue = random.nextInt(90) + 10;
            total += randomValue;

            callables.add(() -> {
                        long from = 1L;
                        long to = 2L;
                        try {
                            if (randomValue > 40) {
                                long temp = from;
                                from = to;
                                to = temp;
                            }
                            return operationService.doOperation(
                                    OperationRestReq.builder()
                                            .from(from)
                                            .to(to)
                                            .amount(BigDecimal.valueOf(randomValue))
                                            .build()
                            );
                        } catch (BaseException e) {
                            fail(e.getMessage());
                        }
                        return null;
                    }
            );
        }
        List<Future<Operation>> futures = executor.invokeAll(callables);

        Operation operation = futures.get(0).get(500, TimeUnit.MILLISECONDS);
        BigDecimal toBalance = operation.getTo().getBalance();
        BigDecimal fromBalance = operation.getFrom().getBalance();
        BigDecimal result = toBalance.add(fromBalance).subtract(BigDecimal.valueOf(30_000));
        assertEquals(BigDecimal.ZERO, result);


        BigDecimal operationTotal = BigDecimal.ZERO;
        for (Future<Operation> future : futures) {
            Operation tmpOperation = future.get(500, TimeUnit.MILLISECONDS);
            operationTotal = operationTotal.add(tmpOperation.getAmount());
        }

        assertEquals(BigDecimal.valueOf(total), operationTotal);
    }
}