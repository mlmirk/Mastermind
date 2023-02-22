package com.mastermind.app;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class MastermindApp {
    //initialize scanner to capture input from user
    private final Scanner scanner = new Scanner(System.in);
    private String guess;
    private int turn = 0;
    private String[] secret = null;
    private final String WELCOME = "Welcome to Mastermind can you guess the code????";
    private boolean gameOver = false;
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    //execute will direct application flow
    public void execute(){
        Thread newThread = new Thread(this::getRandomCode);
        //create a new thread to fetch the secret number and let the program run while it resolves
        newThread.start();
        welcome();
        do {
        getUserInput();
            System.out.println(gameOver);
        }while (turn < 9  && !gameOver);


    }

    private void getUserInput() {
        System.out.println("Enter a 4 digit number and try and guess the code using 0-7");
        //standardize all incoming messages from the user
        guess = scanner.nextLine().toLowerCase();
        if(isSecretWord(guess)){
            gameOver = true;
        }
        turn++;

    }

    private boolean isSecretWord(String guess) {
        String[] guessArray =  guess.split("");
        for (int i = 0; i < guessArray.length; i++) {
            if(!guessArray[i].equals(secret[i])){
                return false;
            }
        }
        return true;

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
            parseAndSetSecret(body);
            System.out.println(Arrays.toString(secret));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseAndSetSecret(String body) {
        // take the response body string remove all spaces and set an array
        // containing string representation of 4 digit secret code

        secret = body.replaceAll("\\s+", "").split("");
    }

    private void welcome() {
        System.out.println(WELCOME);
    }

}
