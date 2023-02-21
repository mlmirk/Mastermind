package com.mastermind.app;

import java.util.Scanner;

public class MastermindApp {
    //initialize scanner to capture input from user
    private final Scanner scanner = new Scanner(System.in);
    private int turn = 0;
    private final String WELCOME = "Welcome to Mastermind can you guess the code????";

    //execute will direct application flow
    public void execute(){
        getRandomCode();
        welcome(); 

    }

    private void getRandomCode() {
    }

    private void welcome() {
        System.out.println(WELCOME);
    }

}
