package com.open.tool.revolut.controller.handler;

import com.open.tool.revolut.controller.SparkBase;
import com.open.tool.revolut.model.rest.req.AccountRestReq;
import com.open.tool.revolut.model.rest.res.AccountRestRes;
import com.open.tool.revolut.model.rest.res.ErrorRes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountControllerTest extends SparkBase {

    private HttpClient client = HttpClient.newHttpClient();

    @BeforeAll
    public static void setUp() {
        runServer();
    }

    @AfterAll
    public static void tearDown() {
        stopServer();
    }


    @Test
    @Order(1)
    public void creatingAccountAndCheck() throws URISyntaxException, IOException, InterruptedException {
        AccountRestRes actual = AccountRestRes.builder()
                .balance(BigDecimal.ZERO)
                .name("testAccount")
                .id(1L)
                .build();

        AccountRestRes accountRestRes = createAccount(client, "testAccount");

        assertEquals(accountRestRes, actual);

        accountRestRes = getAccount(client, accountRestRes.getId());

        assertEquals(accountRestRes, actual);

    }

    @Test
    @Order(2)
    public void creatingDuplicateAccountAndCheck() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(accountUri))
                .POST(HttpRequest.BodyPublishers.ofString(
                        gson.toJson(AccountRestReq.builder()
                                .name("testAccount")
                                .build()
                        ))
                )
                .build();

        HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        ErrorRes errorRes = gson.fromJson(postResponse.body(), ErrorRes.class);

        assertEquals("Account with name=testAccount exist", errorRes.getMessage());

    }
}
