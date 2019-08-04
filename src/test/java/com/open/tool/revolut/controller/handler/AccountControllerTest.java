package com.open.tool.revolut.controller.handler;

import com.open.tool.revolut.controller.SparkBase;
import com.open.tool.revolut.model.rest.req.AccountRestReq;
import com.open.tool.revolut.model.rest.res.AccountRestRes;
import com.open.tool.revolut.model.rest.res.ErrorRes;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.Assert.assertEquals;

public class AccountControllerTest extends SparkBase {

    @Test
    public void creatingAccountAndCheck() throws URISyntaxException, IOException, InterruptedException {
        AccountRestRes actual = AccountRestRes.builder()
                .balance(BigDecimal.ZERO)
                .name("testAccount")
                .id(1L)
                .build();


        HttpClient client = HttpClient.newHttpClient();

        AccountRestRes accountRestRes = createAccount(client, "testAccount");

        assertEquals(accountRestRes, actual);

        accountRestRes = getAccount(client, accountRestRes.getId());

        assertEquals(accountRestRes, actual);

    }
    @Test
    public void creatingDuplicateAccountAndCheck() throws URISyntaxException, IOException, InterruptedException {
        AccountRestRes actual = AccountRestRes.builder()
                .balance(BigDecimal.ZERO)
                .name("testAccount")
                .id(1L)
                .build();


        HttpClient client = HttpClient.newHttpClient();

        AccountRestRes accountRestRes = createAccount(client, "testAccount");

        assertEquals(accountRestRes, actual);

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
