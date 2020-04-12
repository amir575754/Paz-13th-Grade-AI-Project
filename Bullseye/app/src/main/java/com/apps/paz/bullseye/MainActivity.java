package com.apps.paz.bullseye;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.apps.paz.bullseye.ai_util.AI_Util;
import com.apps.paz.bullseye.enums.ColorEnum;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener { //The activity in which the game happens.

    TextView[] guessGradeTextViews; //A TextView array of 10 elements in order to show the user the grade for his guesses.
    Button[][] guessButtons; //A 2D Button array of 40 elements (10 * 4) in order to receive the user's guess.
    ColorEnum[][] guessButtonsColor; //A 2D ColorEnum array of 40 elements (10 * 4) in order to know what is the current color of each button. Corresponds to the array above.
    Button btnGuess; //The button through which the user submits his guess.
    Button btnGiveUp; //The button through which the user gives up and goes back to the Welcome Activity.

    boolean userWon = false; //A boolean that helps the game know if the user has won the game. True - won, False - hasn't won (false doesn't necessarily mean the user lost).
    boolean AIwon = false; //A boolean that helps the game know if the AI has won the game. True - won, False - hasn't won (false doesn't necessarily mean the AI lost).

    int currentGuessIndex; //An int which helps to keep track of how many guesses the user has made.

    Dialog gameStateDialog; //A dialog for the gameState - who won or a tie.
    TextView tvGameState; //The text view that shows the game's result.
    Button btnRestart; //a button that allows the user to restart when the game has ended.

    Dialog computerGuessDialog; //A dialog to show the AI's guess.
    TextView[] computerGuessTextViews; //A TextView array of 4 elements used to show what colors the AI chose as its guess.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentGuessIndex = 0; //A new game means the user has made 0 guesses so far.
        initViews(); //Take control of all the design elements in the activity.

        final ScrollView scrollview = ((ScrollView) findViewById(R.id.mainScrollView)); //Scroll the scrollView to the bottom.
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void initViews() {
        initArrays(); //Init the TextView and button arrays.

        guessButtonsColor = new ColorEnum[GameUtil.GAME_HEIGHT][GameUtil.GAME_WIDTH]; //Init the buttonColor array.

        for (int i = 0; i < guessButtons.length; i++) { //Loop over the guessButtons array
            for (int j = 0; j < guessButtons[i].length; j++) {
                guessButtons[i][j].setOnClickListener(this); //Add a click listener to each button
                if (i != 0) { //If it's not the first row of buttons
                    guessButtons[i][j].setEnabled(false); //Set the button's enabled to false. The first row is meant for the first guess. The user shouldn't be touching the other rows until he's reached the proper guessIndex.
                }
            }
        }

        btnGuess = (Button) findViewById(R.id.btnGuess); //Take control of btnGuess.
        btnGiveUp = (Button) findViewById(R.id.btnGiveUp); //Take control of btnGiveUp.

        btnGuess.setOnClickListener(this); //Add click listener to btnGuess.
        btnGiveUp.setOnClickListener(this); //Add click listener to btnGiveUp.
    }

    private void initArrays() {
        //Initializes the guessGrade TextView array
        guessGradeTextViews = new TextView[GameUtil.GAME_HEIGHT];
        guessGradeTextViews[0] = (TextView) findViewById(R.id.tvGuess1);
        guessGradeTextViews[1] = (TextView) findViewById(R.id.tvGuess2);
        guessGradeTextViews[2] = (TextView) findViewById(R.id.tvGuess3);
        guessGradeTextViews[3] = (TextView) findViewById(R.id.tvGuess4);
        guessGradeTextViews[4] = (TextView) findViewById(R.id.tvGuess5);
        guessGradeTextViews[5] = (TextView) findViewById(R.id.tvGuess6);
        guessGradeTextViews[6] = (TextView) findViewById(R.id.tvGuess7);
        guessGradeTextViews[7] = (TextView) findViewById(R.id.tvGuess8);
        guessGradeTextViews[8] = (TextView) findViewById(R.id.tvGuess9);
        guessGradeTextViews[9] = (TextView) findViewById(R.id.tvGuess10);

        //Initializes the guess button array
        guessButtons = new Button[GameUtil.GAME_HEIGHT][GameUtil.GAME_WIDTH];
        guessButtons[0][0] = (Button) findViewById(R.id.btnGuess1Color1);
        guessButtons[0][1] = (Button) findViewById(R.id.btnGuess1Color2);
        guessButtons[0][2] = (Button) findViewById(R.id.btnGuess1Color3);
        guessButtons[0][3] = (Button) findViewById(R.id.btnGuess1Color4);

        guessButtons[1][0] = (Button) findViewById(R.id.btnGuess2Color1);
        guessButtons[1][1] = (Button) findViewById(R.id.btnGuess2Color2);
        guessButtons[1][2] = (Button) findViewById(R.id.btnGuess2Color3);
        guessButtons[1][3] = (Button) findViewById(R.id.btnGuess2Color4);

        guessButtons[2][0] = (Button) findViewById(R.id.btnGuess3Color1);
        guessButtons[2][1] = (Button) findViewById(R.id.btnGuess3Color2);
        guessButtons[2][2] = (Button) findViewById(R.id.btnGuess3Color3);
        guessButtons[2][3] = (Button) findViewById(R.id.btnGuess3Color4);

        guessButtons[3][0] = (Button) findViewById(R.id.btnGuess4Color1);
        guessButtons[3][1] = (Button) findViewById(R.id.btnGuess4Color2);
        guessButtons[3][2] = (Button) findViewById(R.id.btnGuess4Color3);
        guessButtons[3][3] = (Button) findViewById(R.id.btnGuess4Color4);

        guessButtons[4][0] = (Button) findViewById(R.id.btnGuess5Color1);
        guessButtons[4][1] = (Button) findViewById(R.id.btnGuess5Color2);
        guessButtons[4][2] = (Button) findViewById(R.id.btnGuess5Color3);
        guessButtons[4][3] = (Button) findViewById(R.id.btnGuess5Color4);

        guessButtons[5][0] = (Button) findViewById(R.id.btnGuess6Color1);
        guessButtons[5][1] = (Button) findViewById(R.id.btnGuess6Color2);
        guessButtons[5][2] = (Button) findViewById(R.id.btnGuess6Color3);
        guessButtons[5][3] = (Button) findViewById(R.id.btnGuess6Color4);

        guessButtons[6][0] = (Button) findViewById(R.id.btnGuess7Color1);
        guessButtons[6][1] = (Button) findViewById(R.id.btnGuess7Color2);
        guessButtons[6][2] = (Button) findViewById(R.id.btnGuess7Color3);
        guessButtons[6][3] = (Button) findViewById(R.id.btnGuess7Color4);

        guessButtons[7][0] = (Button) findViewById(R.id.btnGuess8Color1);
        guessButtons[7][1] = (Button) findViewById(R.id.btnGuess8Color2);
        guessButtons[7][2] = (Button) findViewById(R.id.btnGuess8Color3);
        guessButtons[7][3] = (Button) findViewById(R.id.btnGuess8Color4);

        guessButtons[8][0] = (Button) findViewById(R.id.btnGuess9Color1);
        guessButtons[8][1] = (Button) findViewById(R.id.btnGuess9Color2);
        guessButtons[8][2] = (Button) findViewById(R.id.btnGuess9Color3);
        guessButtons[8][3] = (Button) findViewById(R.id.btnGuess9Color4);

        guessButtons[9][0] = (Button) findViewById(R.id.btnGuess10Color1);
        guessButtons[9][1] = (Button) findViewById(R.id.btnGuess10Color2);
        guessButtons[9][2] = (Button) findViewById(R.id.btnGuess10Color3);
        guessButtons[9][3] = (Button) findViewById(R.id.btnGuess10Color4);
    }

    @Override
    public void onClick(View view) {
        if (view == btnGuess) {
            boolean result = makeUserTurn(); //Submit the user's guess and make his turn, if the guess was valid, result will have true in it, otherwise, false.
            if(!userWon && result) { //If the user hasn't won in the turn he has just made and the guess he made was valid.
                makeAITurn(); //Make the AI's turn.
            }
        } else if (view == btnGiveUp) {
            createRestartDialog(); //Create a restart Alert Dialog ("Do you want to restart? Yes or no..")
        } else if (view == btnRestart) {
            restartGame(); //Restart the game.
        } else { //A button in the guessButtons array was pressed.
            for (int i = 0; i < guessButtons.length; i++) { //Loop over the guessButtons array.
                for (int j = 0; j < guessButtons[i].length; j++) {
                    if (guessButtons[i][j] == view) { //If you found the button pressed in the array.
                        guessButtonsColor[i][j] = GameUtil.getNextColor(guessButtonsColor[i][j]); //Get the next color for the button pressed based on its current color.
                        switch (guessButtonsColor[i][j]) { //Translate from colorEnum to real life color.
                            case Pink:
                                guessButtons[i][j].setBackgroundColor(Color.MAGENTA);
                                break;
                            case Red:
                                guessButtons[i][j].setBackgroundColor(Color.RED);
                                break;
                            case Green:
                                guessButtons[i][j].setBackgroundColor(Color.GREEN);
                                break;
                            case Blue:
                                guessButtons[i][j].setBackgroundColor(Color.BLUE);
                                break;
                            case Yellow:
                                guessButtons[i][j].setBackgroundColor(Color.YELLOW);
                                break;
                            case Orange:
                                guessButtons[i][j].setBackgroundColor(Color.rgb(255, 165, 0));
                                break;
                        }
                    }
                }
            }
        }

    }

    private void makeAITurn() {
        if(AI_Util.count < 10) { //If the AI hasn't made 10 turns yet.
            if(AI_Util.count > 0) {
                AI_Util.getCode(); //Have the computer pick a code to guess according to the AI's algorithm.
            } else {
                AI_Util.getRandomCode();
            }
            AI_Util.getGuessGrade(); //Get the grade for the code picked.
            AI_Util.removeWrongGuesses(); //Remove all guesses who don't fit according to the AI's algorithm.

            if(AI_Util.lastComputerGrade == 40) { //If the grade the AI got was 40 (If the AI won).
                AIwon = true; //Put true in AIwon.
                createGameStateDialog(); //Create a dialog to show the game state - that the AI won.
            } else {
                createComputerGuessDialog(); //Create a dialog to show the user what the AI guessed.
                //Show a toast containing useful information about the AI's last turn. Remove for final version.
                Toast.makeText(this, Arrays.toString(AI_Util.lastComputerGuess) + "\n" + AI_Util.lastComputerGrade + "\n" + AI_Util.AI_list.size(), Toast.LENGTH_LONG).show();
            }

            AI_Util.count++; //Increase the turn count for the AI by 1
            currentGuessIndex++; //Increase the turn count for the user by 1.

            if(AI_Util.count >= 10) { //If the turn count of the AI has reached above 10 - both the AI and the user weren't able to guess their codes.
                createGameStateDialog(); //Create a dialog to show the user it's a tie.
            }
        }

    }

    private boolean makeUserTurn() {
        ColorEnum[] colors = new ColorEnum[4]; //Create a colorEnum array representing the user's guess.
        //Take the colors the user chose and insert them to the colors array.
        colors[0] = guessButtonsColor[AI_Util.count][0];
        colors[1] = guessButtonsColor[AI_Util.count][1];
        colors[2] = guessButtonsColor[AI_Util.count][2];
        colors[3] = guessButtonsColor[AI_Util.count][3];


        Integer[] userGuess = GameUtil.translateGuessToNumberArray(colors); //Translate the colors the user chose, to numbers and put them inside the userGuess array.

        boolean isValid = true; //A boolean used to check if the user's guess is valid.
        for (int i = 0; i < userGuess.length && isValid; i++) { //Loop over the userGuess array while nothing has been found to show the guess is invalid.
            if (userGuess[i] == 0) { //If one of the digits is 0 - the button's color was default, which means the user didn't pick all 4 colors.
                isValid = false; //The user's guess is invalid - put false in isValid.
            }
        }
        isValid = isValid && AI_Util.areDistinct(userGuess); //Check if the digits in the user's guess are different from one another.
        if (isValid) { //If the user's guess was valid
            int grade = GameUtil.getGuessGrade(userGuess); //Grade the user's guess.
            guessGradeTextViews[AI_Util.count].setText(grade + ""); //Show the grade in the corresponding TextView (It's in the same row as the buttons he used to guess).
            if(grade == 40) { //If the grade is 40 - the user won.
                userWon = true; //Put true in userWon.
                btnGuess.setEnabled(false); //Set the guess button's enabled to false - stop the user from guessing another guess because he's won.
                createGameStateDialog(); //Create a dialog to show the user has won
            } else { //If the user hasn't won.
                if (AI_Util.count < 9) { //If the user hasn't made his final guess yet (the 10th guess).
                    for (int i = 0; i < GameUtil.GAME_WIDTH; i++) { //Loop over the current row of guess buttons
                        guessButtons[AI_Util.count][i].setEnabled(false); //Set the enabled of the buttons in the current row to false - so the user won't be able to press them anymore.
                        guessButtons[AI_Util.count + 1][i].setEnabled(true); //Set the enabled of the buttons in the next row to true - so the user will be able to press them.
                    }
                }
            }
        } else { //The user's guess was invalid.
            Toast.makeText(this, "Please make sure your guess is valid!", Toast.LENGTH_LONG).show(); //Show a toast to the user, so he knows his guess wasn't valid.
        }
        return isValid; //Return true if the user's turn was successful, otherwise - false.
    }

    private void createGameStateDialog() {
        //Create a dialog to show the game state, AI won/user Won/Tie.
        gameStateDialog = new Dialog(this);
        gameStateDialog.setContentView(R.layout.game_state_dialog);
        gameStateDialog.setTitle("Game State");
        gameStateDialog.setCancelable(true);

        btnRestart = (Button) gameStateDialog.findViewById(R.id.btnRestart);
        tvGameState = (TextView) gameStateDialog.findViewById(R.id.tvGameState);
        if(userWon && !AIwon) { //If the user won and the AI didn't
            tvGameState.setText("You Won"); //Set the text in the dialog's text view to "You Won"
        } else if(!userWon&&AIwon) { //If the user didn't win and the AI did.
            tvGameState.setText("The Computer Won"); //Set the text in the dialog's text view to "AI Won"
        }

        btnRestart.setOnClickListener(this); //Add a click listener to the restart button in the dialog.

        gameStateDialog.show(); //Show the dialog.
    }

    private void createComputerGuessDialog() {
        //Create a dialog to show the user the AI's last guess.
        computerGuessDialog = new Dialog(this);
        computerGuessDialog.setContentView(R.layout.computer_guess_dialog);
        computerGuessDialog.setTitle("Computer Guess");
        computerGuessDialog.setCancelable(true);

        //Take control over the TextView array of the colors for the AI's guess.
        computerGuessTextViews = new TextView[4];
        computerGuessTextViews[0] = computerGuessDialog.findViewById(R.id.tvGuessColor1);
        computerGuessTextViews[1] = computerGuessDialog.findViewById(R.id.tvGuessColor2);
        computerGuessTextViews[2] = computerGuessDialog.findViewById(R.id.tvGuessColor3);
        computerGuessTextViews[3] = computerGuessDialog.findViewById(R.id.tvGuessColor4);

        for (int i = 0; i < computerGuessTextViews.length; i++) { //Loop over the AI's guess and the TextView array.
            switch(AI_Util.lastComputerGuess[i]) { //Set the color of the text view according to the corresponding digit in the code.
                case 1:
                    computerGuessTextViews[i].setBackgroundColor(Color.MAGENTA);
                    break;
                case 2:
                    computerGuessTextViews[i].setBackgroundColor(Color.RED);
                    break;
                case 3:
                    computerGuessTextViews[i].setBackgroundColor(Color.GREEN);
                    break;
                case 4:
                    computerGuessTextViews[i].setBackgroundColor(Color.BLUE);
                    break;
                case 5:
                    computerGuessTextViews[i].setBackgroundColor(Color.rgb(255, 165, 0));
                    break;
                case 6:
                    computerGuessTextViews[i].setBackgroundColor(Color.YELLOW);
                    break;
            }
        }

        computerGuessDialog.show(); //Show the dialog.
    }

    //This is a function that defines what to do when the user pressed the back button.
    //The default is to return to the previous screen, but we want the user to be able to go back to the welcome screen only through the give up or restart buttons.
    //I've overridden the function, so it does nothing when back is pressed, thus stopping the user from going back to the welcome activity no through the buttons.
    @Override
    public void onBackPressed() {

    }

    private void restartGame() {
        AI_Util.count = 0; //Set the turn count of the AI to 0.
        AI_Util.init_ai_list(); //Re-Initialize the AI guess list.
        currentGuessIndex = 0; //Set the turn count of the user to 0.

        Intent intent = new Intent(this, WelcomeActivity.class); //Go back to the welcome activity.
        startActivity(intent);
    }

    private void createRestartDialog() {
        //Create a restart dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restart");
        builder.setMessage("Do you want restart the game?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new HandleAlertDialogListener());
        builder.setNegativeButton("No", new HandleAlertDialogListener());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //A class to listen to the user's choice - restart or not.
    private class HandleAlertDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if(i == -1) { //If the user chose yes - to restart the game.
                restartGame();
            }
        }
    }
}
