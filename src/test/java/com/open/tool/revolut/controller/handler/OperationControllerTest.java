package com.open.tool.revolut.controller.handler;

import com.google.gson.reflect.TypeToken;
import com.open.tool.revolut.controller.SparkBase;
import com.open.tool.revolut.model.rest.req.OperationRestReq;
import com.open.tool.revolut.model.rest.res.AccountRestRes;
import com.open.tool.revolut.model.rest.res.ErrorRes;
import com.open.tool.revolut.model.rest.res.OperationRestRes;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class OperationControllerTest extends SparkBase {


    @Test
    public void createOperations() throws InterruptedException, IOException, URISyntaxException {
        HttpClient client = HttpClient.newHttpClient();

        AccountRestRes first = createAccount(client, "testAccount");


        //Making deposit to account with zero balance
        OperationRestReq request = OperationRestReq.builder()
                .to(first.getId())
                .amount(BigDecimal.valueOf(100))
                .build();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(operationUri + "/deposit"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .build();

        HttpResponse<String> getResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        OperationRestRes response = gson.fromJson(getResponse.body(), OperationRestRes.class);

        assertEquals(0, request.getAmount().compareTo(response.getAmount()));

        //Making transfer from account with money to empty account
        AccountRestRes second = createAccount(client, "testAccount1");
        double val = 13.67;
        response = doTransfer(client, first, second, val);

        assertEquals(0,
                BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(val))
                        .compareTo(BigDecimal.valueOf(100).subtract(response.getAmount())));


        //Making negative transfer
        httpRequest = HttpRequest.newBuilder()
                .uri(new URI(operationUri))
                .POST(HttpRequest.BodyPublishers.ofString(
                        gson.toJson(
                                OperationRestReq.builder()
                                        .from(first.getId())
                                        .to(second.getId())
                                        .amount(BigDecimal.valueOf(-16.35))
                                        .build())
                        )
                )
                .build();


        getResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ErrorRes errorRes = gson.fromJson(getResponse.body(), ErrorRes.class);

        assertEquals("Amount must be > 0", errorRes.getMessage());

        //Making a very big amount withdraw from account
        httpRequest = HttpRequest.newBuilder()
                .uri(new URI(operationUri))
                .POST(HttpRequest.BodyPublishers.ofString(
                        gson.toJson(OperationRestReq.builder()
                                .from(first.getId())
                                .to(second.getId())
                                .amount(BigDecimal.valueOf(1000))
                                .build())
                        )
                )
                .build();

        getResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        errorRes = gson.fromJson(getResponse.body(), ErrorRes.class);

        assertEquals("Account don't have enough money", errorRes.getMessage());


        //Making withdraw from nonexistent account
        httpRequest = HttpRequest.newBuilder()
                .uri(new URI(operationUri))
                .POST(HttpRequest.BodyPublishers.ofString(
                        gson.toJson(OperationRestReq.builder()
                                .from(999L)
                                .to(second.getId())
                                .amount(BigDecimal.valueOf(10))
                                .build())
                        )
                )
                .build();

        getResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        errorRes = gson.fromJson(getResponse.body(), ErrorRes.class);

        assertEquals("Sender account with id: 999 not found", errorRes.getMessage());


        //Making transfer to nonexistent account
        httpRequest = HttpRequest.newBuilder()
                .uri(new URI(operationUri))
                .POST(HttpRequest.BodyPublishers.ofString(
                        gson.toJson(OperationRestReq.builder()
                                .from(first.getId())
                                .to(999L)
                                .amount(BigDecimal.valueOf(10))
                                .build())
                        )
                )
                .build();

        getResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        errorRes = gson.fromJson(getResponse.body(), ErrorRes.class);

        assertEquals("Recipient account with id: 999 not found", errorRes.getMessage());

        //Check list of operations
        for (int i = 0; i < 10; i++) {
            doTransfer(client, first, second, (i + 1));
        }
        httpRequest = HttpRequest.newBuilder()
                .uri(new URI(accountUri + "/" + first.getId() + "/operations"))
                .GET()
                .build();

        getResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        List<OperationRestRes> operations = gson.fromJson(getResponse.body(), new TypeToken<List<OperationRestRes>>(){}.getType());
        assertFalse(operations.isEmpty());
        assertEquals(12, operations.size());
    }

    private OperationRestRes doTransfer(HttpClient client, AccountRestRes first, AccountRestRes second, double val) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest httpRequest;
        HttpResponse<String> getResponse;
        httpRequest = HttpRequest.newBuilder()
                .uri(new URI(operationUri))
                .POST(HttpRequest.BodyPublishers.ofString(
                        gson.toJson(
                                OperationRestReq.builder()
                                        .from(first.getId())
                                        .to(second.getId())
                                        .amount(BigDecimal.valueOf(val))
                                        .build())
                        )
                )
                .build();


        getResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(getResponse.body(), OperationRestRes.class);
    }

}
