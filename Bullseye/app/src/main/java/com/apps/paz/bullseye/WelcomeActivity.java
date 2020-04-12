package com.apps.paz.bullseye;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.apps.paz.bullseye.ai_util.AI_Util;
import com.apps.paz.bullseye.enums.ColorEnum;

import java.util.Random;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnStartGame; //A button through which the user starts the game.
    Button btnInstructions; //A button through which the user opens the instructions dialog.

    Button btnStartGameDialog; //A button through which the user can start the game after he's picked the code for the AI to guess.
    Button[] btnPickColors; //An array of buttons through which the user can choose the AI's code.
    ColorEnum[] colors; //An array of colors representing the AI's code in colors.

    Dialog dialogPickCode; //The dialog through which the user picks the AI's code and starts the game.

    Dialog dialogInstructions; //The instructions dialog.
    TextView tvInstructions; //The TextView  that shows the instructions.
    Button btnNextInstruction; //The next button to go to the next instruction.
    Button btnPreviousInstruction; //The previous button to go to the previous instruction.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initViews(); //Take control of all design elements.
        GameUtil.initInstructions(); //Init the instructions list.
    }

    private void initViews() {
        btnStartGame = (Button) findViewById(R.id.btnStartGame); //Take control of the start game button.
        btnInstructions = (Button) findViewById(R.id.btnInstructions); //Take control of the instructions button.

        btnStartGame.setOnClickListener(this); //Add a click listener to the start game button.
        btnInstructions.setOnClickListener(this); //Add a click listener to the instructions button.

        GameUtil.initColorChain(); //Init the game's color chain - the circular linked list containing the possible code colors.
        AI_Util.init_ai_list(); //Init the AI's guess list.
    }

    @Override
    public void onClick(View view) {
        if (view == btnStartGame) {
            createPickCodeDialog(); //Create a dialog asking the user to pick a code for the AI.
        } else if (view == btnInstructions) {
            createInstructionsDialog(); //Create a dialog that shows the user the game instructions.
        } else if (view == btnPreviousInstruction) {
            if(GameUtil.currentInstructionIndex > 0) { //If the current instruction index is above 0
                GameUtil.currentInstructionIndex--; //Decrease the current instruction index by 1.
                tvInstructions.setText(GameUtil.instructionsList.get(GameUtil.currentInstructionIndex)); //Show the new instruction.
                btnNextInstruction.setText("Next"); //Now that the instruction isn't the final instruction, set the next button text to "Next".
            }
        } else if (view == btnNextInstruction) {
            if(GameUtil.currentInstructionIndex < GameUtil.instructionsList.size() - 1) { //If the instruction index hasn't reached the last part.
                GameUtil.currentInstructionIndex++; //Increase the current instruction index by 1.
                tvInstructions.setText(GameUtil.instructionsList.get(GameUtil.currentInstructionIndex)); //Show the new instruction.
                if(GameUtil.currentInstructionIndex == GameUtil.instructionsList.size() - 1) { //If the instruction index has reached the last part.
                    btnNextInstruction.setText("Go to game"); //Change the next button's text to "Go to game".
                }
            } else { //The user chose Go to game.
                dialogInstructions.dismiss(); //Close the instructions dialog.
                createPickCodeDialog(); //Open a dialog asking the user to pick a code for the AI.
            }
        } else if (view == btnStartGameDialog) {
            AI_Util.count = 0; //Set the AI turn count to 0.

            //Pick a random code for the user to guess from the AI's guess list (which by default, contains all possible codes).
            Random r = new Random();
            int index = r.nextInt(359 - 0 + 1) + 0;
            GameUtil.answer = AI_Util.AI_list.get(index);

            Integer[] userGuess = GameUtil.translateGuessToNumberArray(colors); //Take the code the user chose for the AI.

            //Check if the user picked colors and didn't leave the default color.
            boolean isValid = true;
            for (int i = 0; i < userGuess.length && isValid; i++) {
                if (userGuess[i] == 0) {
                    isValid = false;
                }
            }
            isValid = isValid && AI_Util.areDistinct(userGuess); //Check if the user chose 4 different colors.
            if (isValid) { //If the code the user chose for the AI is valid.
                AI_Util.answer = userGuess; //Update the code in AI_Util class.
                Intent intent = new Intent(this, MainActivity.class); //Go to the game screen (MainActivity).
                startActivity(intent);
            } else { //If the code the user chose for the AI isn't valid.
                Toast.makeText(this, "Please make sure your code is valid!", Toast.LENGTH_LONG).show(); //Show the user a toast asking him to enter a valid code.
            }
        } else { //If the 4 color buttons of the dialog were pressed.
            for (int i = 0; i < btnPickColors.length; i++) { //Loop over the btnPickColor array.
                if (view == btnPickColors[i]) { //If the pressed button was found.
                    colors[i] = GameUtil.getNextColor(colors[i]); //Get the next color for the pressed button, but as a colorEnum.
                    //Translate the button's color from colorEnum to real life color.
                    switch (colors[i]) {
                        case Pink:
                            btnPickColors[i].setBackgroundColor(Color.MAGENTA);
                            break;
                        case Red:
                            btnPickColors[i].setBackgroundColor(Color.RED);
                            break;
                        case Green:
                            btnPickColors[i].setBackgroundColor(Color.GREEN);
                            break;
                        case Blue:
                            btnPickColors[i].setBackgroundColor(Color.BLUE);
                            break;
                        case Yellow:
                            btnPickColors[i].setBackgroundColor(Color.YELLOW);
                            break;
                        case Orange:
                            btnPickColors[i].setBackgroundColor(Color.rgb(255, 165, 0));
                            break;
                    }
                }
            }
        }
    }

    private void createPickCodeDialog() {
        //Create a dialog for the user to pick the AI's code.
        dialogPickCode = new Dialog(this);
        dialogPickCode.setContentView(R.layout.pick_code_layout);
        dialogPickCode.setTitle("Choose Code");
        dialogPickCode.setCancelable(false);
        btnStartGameDialog = (Button) dialogPickCode.findViewById(R.id.btnStartGameDialog);

        //Take control of the pick color buttons.
        btnPickColors = new Button[GameUtil.GAME_WIDTH];
        btnPickColors[0] = (Button) dialogPickCode.findViewById(R.id.btnPickColor1);
        btnPickColors[1] = (Button) dialogPickCode.findViewById(R.id.btnPickColor2);
        btnPickColors[2] = (Button) dialogPickCode.findViewById(R.id.btnPickColor3);
        btnPickColors[3] = (Button) dialogPickCode.findViewById(R.id.btnPickColor4);

        colors = new ColorEnum[GameUtil.GAME_WIDTH]; //Init the array representing the AI code array to 4 elements.

        btnStartGameDialog.setOnClickListener(this); //Add click listener to the start game button.
        for (int i = 0; i < btnPickColors.length; i++) { //Loop over the pick color buttons.
            btnPickColors[i].setOnClickListener(this); //Add click listener to the pick color buttons.
        }

        dialogPickCode.show(); //Show the pick code dialog.
    }

    private void createInstructionsDialog() {
        //Create a new instructions dialog.
        dialogInstructions = new Dialog(this);
        dialogInstructions.setContentView(R.layout.instructions_dialog);
        dialogInstructions.setTitle("Instructions");
        dialogInstructions.setCancelable(false);
        tvInstructions = (TextView) dialogInstructions.findViewById(R.id.tvInstructions);
        btnNextInstruction = (Button) dialogInstructions.findViewById(R.id.btnNextInstruction);
        btnPreviousInstruction = (Button) dialogInstructions.findViewById(R.id.btnPreviousInstruction);

        btnNextInstruction.setOnClickListener(this);
        btnPreviousInstruction.setOnClickListener(this);

        dialogInstructions.show(); //Show the dialog to the user.
    }
}
