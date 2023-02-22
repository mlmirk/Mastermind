package com.mastermind.app;

import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class MastermindApp {
    //initialize scanner to capture input from user
    private final Scanner scanner = new Scanner(System.in);
    private int turn = 0;
    private final String WELCOME = "Welcome to Mastermind can you guess the code????";
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    //execute will direct application flow
    public void execute(){
        Thread newThread = new Thread(this::getRandomCode);
        //create a new thread to fetch the secret number
        newThread.start();

        welcome(); 

    }

    private void getRandomCode() {
        //TODO more effective error catching may chaining and priniting based on failure
       final String url = "https://www.random.org/integers/?num=4&min=0&max=7&col=4&base=10&format=plain&rnd=new";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(url))
                .build();
        CompletableFuture<HttpResponse<String>> response = null;
        response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String body = null;
            body = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            System.out.println(body);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void welcome() {
        System.out.println(WELCOME);
    }

}
