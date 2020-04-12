package com.apps.paz.bullseye.ai_util;

import com.apps.paz.bullseye.GameResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AI_Util { //The utilities (functions and variables) the AI needs to play the game

    public static Integer[] answer; //The array which contains the code the AI has to guess. This code is chosen by the user at the start of the game.
    public static int count = 0; //An integer to keep track of the amount of guesses the user made.
    public static ArrayList<Integer[]> AI_list = new ArrayList<>(); //An arrayList containing all possible guesses.
    public static Integer[] lastComputerGuess = new Integer[4]; //An array which contains the last guess the AI has made.
    public static int lastComputerGrade = 0; //An int which contains the grade for the last guess the AI has made.

    public static void getRandomCode() {
        //Input: None
        //Output: puts in lastComputerGuess a random guess from the AI guess list.

        Random r = new Random(); //Define a new random with which to choose a random index in the AI guess list.
        int index = r.nextInt(AI_list.size() - 1 - 0 + 1) + 0; //Draw a random number in the range 0 - AI_list.size() - 1
        lastComputerGuess = AI_list.get(index); //Put the random guess in lastComputerGuess.
        AI_list.remove(index); //Remove the new guess from the guess array.
    }

    public static void getCode() {
        //Input: none
        //Output: the code from the AI guess list that has the best weight

        Random r = new Random(); //If a code's weight is equal to the best weight so far, "flip a coin" in order to decide whether to pick the new code or keep the previous (reminder: they both have the same weight)

        Integer[] bestCode = AI_list.get(0); //Initialize the best code variable with index 0 of the AI guess list.
        int bestWeight = calculateWeight(bestCode); //Initialize the best weight variable with the weight for the code we chose in the line above;
        int currentWeight = bestWeight; //Each new code's weight we check will be at this currentWeight

        for (int i = 1; i < AI_list.size(); i++) { //Loop over the AI guess list starting from index 1, since we already checked index 0 in the lines above.
            currentWeight = calculateWeight(AI_list.get(i)); //Calculate the weight of the current code.
            if(currentWeight > bestWeight) { //If the current code's weight is larger than the best code so far
                bestWeight = currentWeight; //Update the bestWeight variable so it contains the best code's weight
                bestCode = AI_list.get(i); //Update the bestCode variable so it contains the best code
            } else if(currentWeight == bestWeight) { //If the current code's weight is equal to the best code's weight,
                int coin = r.nextInt(1 - 0 + 1) + 0; // Flip a coin to decide whether to update the bestCode or not (they both have the same weight so it doesn't matter.
                if(coin == 1) { //If the coin decided to update the bestCode
                    bestWeight = currentWeight; //Update the bestWeight variable so it contains the best code's weight
                    bestCode = AI_list.get(i); //Update the bestCode variable so it contains the best code
                }
            }
        }
        AI_list.remove(bestCode); //Remove the computer's new guess from the AI guess list.
        lastComputerGuess = bestCode; //Make bestGuess, the computer's guess.
    }

    public static void removeWrongGuesses() {
        //Input: None
        //Output: removes all wrong guesses from the AI guess list.

        for (int i = 0; i < AI_list.size(); i++) { //Loop over the AI guess list
            if (getGrade(lastComputerGuess, AI_list.get(i)) != lastComputerGrade) { //If the grade for the last computer guess and the current guess code in the AI list is different than the last grade the computer received.
                AI_list.remove(AI_list.get(i)); //Remove the current guess from the AI list.
            }
        }
    }

    public static void init_ai_list() {
        //Input: None
        //Output: Initializes AI_list with all the valid guesses containing the digits 1 - 6.
        AI_list.clear();
        for (int i = 1; i < 7; i++) { //Loop over the first digit of the current code
            for (int j = 1; j < 7; j++) { //Loop over the second digit of the current code.
                for (int k = 1; k < 7; k++) { //Loop over the third digit of the current code.
                    for (int l = 1; l < 7; l++) { //Loop over the fourth digit of the current code.
                        Integer[] arr = {i, j, k, l}; //Create a guess array from the 4 digits
                        if (areDistinct(arr)) { //If the array's elements are different from one another
                            AI_list.add(arr); //Add the array to AI_list.
                        }

                    }
                }
            }
        }
    }

    public static boolean areDistinct(Integer arr[]) {
        // Put all array elements in a HashSet
        Set<Integer> s = new HashSet<Integer>(Arrays.asList(arr));

        // If all elements are distinct, size of
        // HashSet should be same array.
        return (s.size() == arr.length);
    }

    public static int getGrade(Integer[] computerGuess, Integer[] anotherGuess) {
        //Input: Receives two codes to calculate the grade for.
        //Output: the grade for the two codes.

        GameResult gameResult = new GameResult(); //Initialize a new GameResult object.

        for (int i = 0; i < computerGuess.length; i++) { //Loop over the computer guess array
            int result = checkColor(anotherGuess, computerGuess[i], i); //Check if the current digit of the computer guess is bang on, hit, or nothing
            if (result == 2) { //If it's bang on
                gameResult.addBangOn(); //Add one bang on to the gameResult object.
            } else if (result == 1) { //If it's a hit
                gameResult.addHits(); //Add one hit to the GameResult object.
            }
        }
        return gameResult.getBangOn() * 10 + gameResult.getHits(); //Calculate and return the grade for the two codes
    }

    public static void getGuessGrade() {
        //Input: None
        //Output: a grade for the last computer guess.
        GameResult gameResult = new GameResult(); //Initializes a GameResult object.


        for (int i = 0; i < lastComputerGuess.length; i++) { //Loop over the last computer guess's digits.
            int result = checkColor(answer, lastComputerGuess[i], i); //Get the score for each digit, 2 - Bang On, 1 - Hit, 0 - None.
            if (result == 2) { //Bang On
                gameResult.addBangOn(); //Add bang on the GameResult object.
            } else if (result == 1) { //Hit
                gameResult.addHits(); //Add hit to the GameResult Object.
            }
        }
        lastComputerGrade = gameResult.getBangOn() * 10 + gameResult.getHits(); //Put the grade for the computer guess in lastComputerGrade variable.
    }

    private static int checkColor(Integer[] code, Integer color, int index) {
        //Input: receives a certain code, one of another code's digits and the index of that digit in the another code's array;
        //Output: returns 0 if the color doesn't exist in the answer, 1 if the color exists in the wrong index, 2 if the color exists in the correct index.
        for (int i = 0; i < code.length; i++) { //Loop over the code received
            if (code[i] == color) { //If color exists in the code array.
                if (i == index) { //If it exists in the same index as the index received - Bang On.
                    return 2;
                } else { //If it exists in a different index than the index received - Hit.
                    return 1;
                }
            }
        }
        return 0; //If it doesn't exist at all.
    }

    private static int calculateWeight(Integer[] code) {
        //Input: an integer array with 4 elements representing a game code.
        //Output: the weight for the code received

        //Read page 4 of the pdf you sent me in order to remember how weight is calculated.
        int weight = 0; // each time a digit of the code appeared in the AI guess list, increase weight's value by 1.
        for (Integer[] listCode : AI_list) { //Loop over the AI guess list
            for (int i = 0; i < code.length; i++) { //Loop over each digit of the code we're calculating the weight for
                for (int j = 0; j < listCode.length; j++) { //Loop over each digit of the current guess in AI guess list we are comparing to the code we're calculating the weight for.
                    if(code[i] == listCode[j]) { //If a certain digit in the code is in the current guess we are checking from the AI list
                        weight++; //Increase count by 1
                    }
                }
            }
        }
        return weight;
    }

    /*
    Integer[] userGuessNumbers = new Integer[4];
        for (int i = 0; i < userGuess.length; i++) {
            switch(userGuess[i]) {
                case Red:
                    userGuessNumbers[i] = 1;
                    break;
                case Pink:
                    userGuessNumbers[i] = 1;
                    break;
                case Green:
                    userGuessNumbers[i] = 1;
                    break;
                case Red:
                    userGuessNumbers[i] = 1;
                    break;
                case Red:
                    userGuessNumbers[i] = 1;
                    break;
                case Red:
                    userGuessNumbers[i] = 1;
                    break;
            }
        }
     */
}
