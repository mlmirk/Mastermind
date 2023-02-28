package com.mastermind.app;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MastermindApp {
    private final Scanner scanner = new Scanner(System.in);
    private String guess;
    private int turn = 0;
    private final int MAXIMUM_NUMBER_OF_GUESSES = 9;
    private final String HELP_COMMAND = "help";
    private final String VIEW_PREVIOUS_GUESSES = "previous";
    private final String WINNING_MESSAGE= "You win! You guessed the answer on attempt ";
    private Map<String, String> history = new TreeMap<>();
    private String[] secret = null;
    private final String WELCOME = "Welcome to Mastermind can you guess the code????";
    private boolean gameOver = false;
    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5))
            .build();
    private final PrintStream out = new PrintStream(System.out);

    public void execute() {
        Thread newThread = new Thread(() -> getRandomCode());
        newThread.start();

        welcome();
        do {
            getUserInput();
        } while (turn < MAXIMUM_NUMBER_OF_GUESSES && !gameOver);



    }

    protected void getUserInput() {
        System.out.println("Enter a 4 digit number and try and guess the code using 0-7");
        guess = scanner.nextLine().toLowerCase();
        if (isValidGuess(guess)) {
            gameOver = isSecretWord(guess);
        }
    }

    protected boolean isValidGuess(String guess) {
        guess = guess.replaceAll("\\s+", "");
        String regex = "[0-7]+";
        if (HELP_COMMAND.equals(guess)) {
            System.out.println("HELP");
        } else if (VIEW_PREVIOUS_GUESSES.contains(guess)) {
            printHistory(out);
        } else if (guess.contains("mirkovic")) {
            System.out.println(Arrays.toString(secret));
        } else return guess.matches(regex) && guess.length() == 4;

        return false;
    }

        public void printHistory(PrintStream out) {
            int i = 1;
            for (Map.Entry<String, String> entry : history.entrySet()) {
                out.println("Guess " + i + " - " + entry.getKey() + " was " + entry.getValue());
                i++;
            }
        }

   protected boolean isSecretWord(String guess) {
       String[] guessArray = guess.split("");
       turn++;

       String[] temp = Arrays.copyOf(secret, 4);
       String computerResponse = generateComputerResponse(guessArray, temp);

       history.put(guess, computerResponse);
       out.println(computerResponse);

       return computerResponse.startsWith("You win!");
   }

    protected String generateComputerResponse(String[] guessArray, String[] temp) {
        int correctPosition = countCorrectPositions(guessArray, temp);
        int exists = countCorrectNumbers(guessArray, temp);

        String computerResponse;
        if(correctPosition == 4) {
            computerResponse = "You win! You guessed the answer on attempt " + turn ;
        }else if (correctPosition == 0 && exists == 0) {
            computerResponse = "All incorrect";
        } else {
            computerResponse = exists + " correct numbers and " + correctPosition + " correct locations";
        }
        return computerResponse;
    }

    protected int countCorrectPositions(String[] guessArray, String[] temp) {
        int count = 0;
        for (int i = 0; i < guessArray.length; i++) {
            if (guessArray[i].equals(temp[i])) {
                count++;
                guessArray[i] = "@";
                temp[i] = "@";
            }
        }
        return count;
    }

    protected int countCorrectNumbers(String[] guessArray, String[] temp) {
        int count = 0;
        for (String s : guessArray) {
            if (s.equals("@")) {
                count++;
            } else {
                for (int secretWord = 0; secretWord < 4; secretWord++) {
                    if (s.equals(temp[secretWord])) {
                        count++;
                        temp[secretWord] = "@";
                        break;
                    }
                }
            }
        }
        return count;
    }

    protected void getRandomCode() {
        final String url = "https://www.random.org/integers/?num=4&min=0&max=7&col=4&base=10&format=plain&rnd=new";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI(url))
                    .build();
            CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String body = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            parseAndSetSecret(body);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            System.err.println("Error fetching random code from server: " + e.getMessage());
            e.printStackTrace();
        } catch (URISyntaxException e) {
            System.err.println("Invalid URL: " + url);
            e.printStackTrace();
        }
    }

    protected void parseAndSetSecret(String body) {
        // take the response body string remove all spaces and set an array
        // containing string representation of 4 digit secret code
        secret = body.replaceAll("\\s+", "").split("");
    }

    protected void welcome() {
        System.out.println(WELCOME);
    }
    public Map<String, String> getHistory() {
        return history;
    }
    public void setSecret(String test){
        secret=test.split("");
    }

}
