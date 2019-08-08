package com.open.tool.revolut.controller.handler;

import com.google.gson.reflect.TypeToken;
import com.open.tool.revolut.controller.SparkBase;
import com.open.tool.revolut.model.rest.req.OperationRestReq;
import com.open.tool.revolut.model.rest.res.AccountRestRes;
import com.open.tool.revolut.model.rest.res.ErrorRes;
import com.open.tool.revolut.model.rest.res.OperationRestRes;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OperationControllerTest extends SparkBase {

    private static HttpClient client = HttpClient.newHttpClient();
    private static AccountRestRes first;
    private static AccountRestRes second;


    @BeforeAll
    public static void setUp() throws Exception {
        runServer();
        first = createAccount(client, "OperationControllerTest");
        second = createAccount(client, "OperationControllerTest1");
    }

    @AfterAll
    public static void tearDown() {
        stopServer();
    }

    @DisplayName("Making deposit to account with zero balance")
    @Test
    @Order(1)
    public void zeroBalanceTest() throws InterruptedException, IOException, URISyntaxException {

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

    }


    @DisplayName("Making transfer from account with money to empty account")
    @Test
    @Order(2)
    public void transferToEmptyAccountTest() throws InterruptedException, IOException, URISyntaxException {

        double val = 13.67;
        OperationRestRes response = doTransfer(client, first, second, val);

        assertEquals(0,
                BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(val))
                        .compareTo(BigDecimal.valueOf(100).subtract(response.getAmount())));
    }

    @DisplayName("Making negative transfer")
    @Test
    @Order(2)
    public void negativeTransfer() throws InterruptedException, IOException, URISyntaxException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
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


        HttpResponse<String> getResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ErrorRes errorRes = gson.fromJson(getResponse.body(), ErrorRes.class);

        assertEquals("Amount must be > 0", errorRes.getMessage());
    }

    @DisplayName("Making a very big amount withdraw from account")
    @Test
    @Order(3)
    public void bigWithdrawTest() throws InterruptedException, IOException, URISyntaxException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
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

        HttpResponse<String> getResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ErrorRes errorRes = gson.fromJson(getResponse.body(), ErrorRes.class);

        assertEquals("Account don't have enough money", errorRes.getMessage());


    }

    @DisplayName("Making withdraw from nonexistent account")
    @Test
    @Order(4)
    public void withdrawFromNonexistentAccountTest() throws InterruptedException, IOException, URISyntaxException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
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

        HttpResponse<String> getResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ErrorRes errorRes = gson.fromJson(getResponse.body(), ErrorRes.class);

        assertEquals("Sender account with id: 999 not found", errorRes.getMessage());

    }

    @DisplayName("Making transfer to nonexistent account")
    @Test
    @Order(5)
    public void transferToNonexistentAccountTest() throws InterruptedException, IOException, URISyntaxException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
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

        HttpResponse<String> getResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        ErrorRes errorRes = gson.fromJson(getResponse.body(), ErrorRes.class);

        assertEquals("Recipient account with id: 999 not found", errorRes.getMessage());

    }

    @DisplayName("Check list of operations")
    @Test
    @Order(6)
    public void checkListOfOperationsTest() throws InterruptedException, IOException, URISyntaxException {


        for (int i = 0; i < 10; i++) {
            doTransfer(client, first, second, (i + 1));
        }
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(new URI(accountUri + "/" + first.getId() + "/operations"))
                .GET()
                .build();

        HttpResponse<String> getResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
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
