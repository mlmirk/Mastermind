package com.mastermind.app;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class MastermindAppTest {
    private MastermindApp mastermindApp;
    private ByteArrayOutputStream out;
    private final String[] DEFAULT_GUESS_ARRAY = {"1","2","3","4"};

    @Before
    public void setUp() {
        mastermindApp = new MastermindApp();
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    public void testIsValidGuess() {
        assertTrue(mastermindApp.isValidGuess("1234"));
        assertTrue(mastermindApp.isValidGuess("0000"));
        assertTrue(mastermindApp.isValidGuess("7777"));
        assertTrue(mastermindApp.isValidGuess("0 0 0 0"));
        assertFalse(mastermindApp.isValidGuess("1 2 3"));
        assertFalse(mastermindApp.isValidGuess("12345"));
        assertFalse(mastermindApp.isValidGuess("abc"));
        assertFalse(mastermindApp.isValidGuess(""));
        assertFalse(mastermindApp.isValidGuess(" "));
        assertFalse(mastermindApp.isValidGuess("help"));
        assertFalse(mastermindApp.isValidGuess("previous"));
        assertFalse(mastermindApp.isValidGuess("mirkovic"));
    }

    @Test
    public void testPrintHistory() {
        Map<String, String> history = mastermindApp.getHistory();
        history.put("0123", "2 correct numbers and 1 correct locations");
        history.put("4567", "All incorrect");
        history.put("7654", "1 correct numbers and 2 correct locations");
        PrintStream printStream = new PrintStream(out);
        mastermindApp.printHistory(printStream);
        String expectedOutput = "Guess 1 - 0123 was 2 correct numbers and 1 correct locations" + System.lineSeparator() +
                "Guess 2 - 4567 was All incorrect" + System.lineSeparator() +
                "Guess 3 - 7654 was 1 correct numbers and 2 correct locations" + System.lineSeparator();
        assertEquals(expectedOutput, out.toString());
    }

    @Test
    public void testCountCorrectPositions() {
        String[] temp = {"1", "3", "2", "4"};
        assertEquals(2, mastermindApp.countCorrectPositions(DEFAULT_GUESS_ARRAY, temp));
    }

    @Test
    public void testCountCorrectNumbers() {
        String[] temp = {"1", "3", "2", "4"};
        assertEquals(4, mastermindApp.countCorrectNumbers(DEFAULT_GUESS_ARRAY, temp));
    }

    @Test
    public void testGenerateComputerResponseAllIncorrect() {
        String[] temp = {"1", "3", "2", "4"};
        String expectedResponse = "4 correct numbers and 2 correct locations";
        String actualResponse = mastermindApp.generateComputerResponse(DEFAULT_GUESS_ARRAY, temp); // call the method on the instance
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void testGenerateComputerResponseCorrectNumbersAndLocations() {

        String[] temp = {"1", "3", "2", "4"};
        String expected = "4 correct numbers and 2 correct locations";
        String actual = mastermindApp.generateComputerResponse(DEFAULT_GUESS_ARRAY, temp);
        assertEquals(expected, actual);
    }

    @Test
    public void testGenerateComputerResponseCorrectNumbersOnly() {
        String[] temp = {"4", "3", "2", "1"};
        String expected = "4 correct numbers and 0 correct locations";
        String actual = mastermindApp.generateComputerResponse(DEFAULT_GUESS_ARRAY, temp);
        assertEquals(expected, actual);
    }

    @Test
    public void testGenerateComputerResponseCorrectLocationsOnly() {
        String[] temp = {"4", "3", "1", "2"};
        String expected = "4 correct numbers and 0 correct locations";
        String actual = mastermindApp.generateComputerResponse(DEFAULT_GUESS_ARRAY, temp);
        assertEquals(expected, actual);
    }

    @Test
    public void testGenerateComputerResponseSomeCorrectNumbersAndLocations() {
        String[] temp = {"1", "3", "5", "2"};
        String expected = "3 correct numbers and 1 correct locations";
        String actual = mastermindApp.generateComputerResponse(DEFAULT_GUESS_ARRAY, temp);
        assertEquals(expected, actual);
    }

    @Test
    public void testPlayGameUserWinsOnFirstTry() {
        String answer = "1234";
        String guess = "1234";
        Map<String, String> expectedHistory = new TreeMap<>();
        expectedHistory.put("1234", "You win! You guessed the answer on attempt 1");
        mastermindApp.setSecret(answer);
        mastermindApp.isSecretWord(guess);
        assertEquals(expectedHistory, mastermindApp.getHistory());
    }
    @Test
    public void testPlayGameUserWinsAfterMultipleTries() {
        String answer = "1234";
        String[] guesses = {"5678", "2341", "4321", "1234"};
        Map<String, String> expectedHistory = new TreeMap<>();
        expectedHistory.put("5678", "All incorrect");
        expectedHistory.put("2341", "4 correct numbers and 0 correct locations");
        expectedHistory.put("4321", "4 correct numbers and 0 correct locations");
        expectedHistory.put("1234", "You win! You guessed the answer on attempt 4");
        mastermindApp.setSecret(answer);
        for (String guess:guesses
             ) {
            mastermindApp.isSecretWord(guess);
        }
        assertEquals(expectedHistory, mastermindApp.getHistory());
    }

    @Test
    public void testPlayGameUserLoses() {
        String answer = "1234";
        String[] guesses = {"5677", "2341", "4321", "7777" ,"7770","7707", "7077", "0777", "7700"};
        Map<String, String> expectedHistory = new TreeMap<>();
        expectedHistory.put("5677", "All incorrect");
        expectedHistory.put("2341", "4 correct numbers and 0 correct locations");
        expectedHistory.put("4321", "4 correct numbers and 0 correct locations");
        expectedHistory.put("7777", "All incorrect");
        expectedHistory.put("7770", "All incorrect");
        expectedHistory.put("7707", "All incorrect");
        expectedHistory.put("7077", "All incorrect");
        expectedHistory.put("0777", "All incorrect");
        expectedHistory.put("7700", "All incorrect");

        expectedHistory.put("1", "1");
        mastermindApp.setSecret(answer);
        for (String guess:guesses
        ) {
            mastermindApp.isSecretWord(guess);
        }
        assertEquals(expectedHistory, mastermindApp.getHistory());
    }






}

