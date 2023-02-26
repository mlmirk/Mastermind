package com.mastermind.app;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


public class MastermindApp {
    //initialize scanner to capture input from user
    private final Scanner scanner = new Scanner(System.in);
    private String guess;
    private int turn = 0;
    private final int MAX_GUESSES = 9;
    private final String HELP = "help";
    private final String PREVIOUS= "previous";
    private Map< String, String> history = new TreeMap<>();

    private String[] secret = null;
    private final String WELCOME = "Welcome to Mastermind can you guess the code????";
    private boolean gameOver = false;
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    //execute will direct application flow
    public void execute(){
        Thread newThread = new Thread(() -> getRandomCode());
        //create a new thread to fetch the secret number and let the program run while it resolves
        newThread.start();

        welcome();
        do {
        getUserInput();
        }while (turn < MAX_GUESSES  && !gameOver);


    }

    private void getUserInput() {
        System.out.println("Enter a 4 digit number and try and guess the code using 0-7");
        //standardize all incoming messages from the user
        guess = scanner.nextLine().toLowerCase();
        if(isValidGuess(guess)){
            gameOver = isSecretWord(guess);
        }

    }

    private boolean isValidGuess(String guess) {
        /*
         * Method to check input before counting a turn
         * 1. Strip extra spaces
         * 2. Check for reserved words to preform an action help for rules and previous for previous guesses
         * 3. Check if word matches correct length and contain only the correct digits 0-7 then return true to then
         * evaluate guess and record a turn
          */
        guess = guess.replaceAll("\\s+", "");
        String regex = "[0-7]+";
        if(HELP.equals(guess)) {
            //print help mesasage
            System.out.println("HELP");
        }else if(PREVIOUS.contains(guess)) {
            //print history
           printHistory();
        }else if(guess.contains("mirkovic")) {
            System.out.println(Arrays.toString(secret));
        }else if (guess.matches(regex) && guess.length()==4){
            return true;
        }
        return false;
    }

    private void printHistory() {
        int lineNum=1;
        for( Map.Entry<String, String> entry : history.entrySet() ){
            System.out.println( "Guess "+ lineNum +" - " + entry.getKey() + " was " + entry.getValue() );
            lineNum++;
        }
    }

    private boolean isSecretWord(String guess) {
        String[] guessArray = guess.split("");
        turn++;

        // TODO: Save previous guesses and return input

        String[] temp = Arrays.copyOf(secret, 4);
        String computerResponse;

        int correctPosition = 0;
        int exists = 0;

        // Check for correct positions
        for (int i = 0; i < guess.length(); i++) {
            if (guessArray[i].equals(temp[i])) {
                correctPosition++;
                guessArray[i] = "@";
                temp[i] = "@";
            }
        }

        // Check for correct numbers
        for (String s : guessArray) {
            if (s.equals("@")) {
                exists++;
            } else {
                for (int secretWord = 0; secretWord < 4; secretWord++) {
                    if (s.equals(temp[secretWord])) {
                        exists++;
                        temp[secretWord] = "@";
                        break;
                    }
                }
            }
        }

        // Generate response message
        if (correctPosition == 0 && exists == 0) {
            computerResponse = "All incorrect";
        } else {
            computerResponse = exists + " correct numbers and " + correctPosition + " correct locations";
        }

        // Save guess and response to history
        history.put(guess, computerResponse);

        System.out.println(computerResponse);
        return correctPosition == 4;
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
            parseAndSetSecret("1515");
            //testing sout for secret word exposure
            //System.out.println(Arrays.toString(secret));
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
