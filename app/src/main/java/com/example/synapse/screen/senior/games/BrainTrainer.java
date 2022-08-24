package com.example.synapse.screen.senior.games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.synapse.R;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BrainTrainer extends AppCompatActivity {

    /* PROPERTY DECLARATIONS */
    Button startBtn; // initialize the button property outside the onCreate method
    CountDownTimer timer; // countdown timer property
    ArrayList<Integer> answers = new ArrayList<Integer>(); // arraylist that will hold the answer
    Random rand = new Random(); // generates random number
    int locationOfCorrectAnswer; // property that will hold the location of correct answer
    TextView remark; // property that will change if the answer chosen is correct or not
    int scoreCtr = 0; // property that will count the score
    int numberOfQuestions = 0; // property that will hold the total number of questions
    TextView scoreTextView; // property that will hold the score textview
    Button playAgainBtn; // property that will hold the playAgain button
    ConstraintLayout gameTransitionLayout; // property that will hold the constraint that will give the illusion that no one is beneath the constraint; pantakip sa loob
    androidx.gridlayout.widget.GridLayout gridLayout; // property that will hold the grid layout
    ArrayList<View> layoutButtons;

    /* ONCLICK LISTENER THAT WILL ALLOW USERS TO PLAY THE GAME AGAIN*/
    public void playAgain(View view){
        playAgainBtn.setVisibility(View.INVISIBLE);

        // reset the game
        enableButtons(); // enable the buttons again
        scoreCtr = 0;
        numberOfQuestions = 0;
        remark.setText("");
        scoreTextView.setText("0/0");
        generateQuestion(); // call the generate question when the screen starts
        timer(); // start the timer immediately

    }

    /* ONCLICK LISTENER THAT WILL SET THE START BUTTON TO INVISIBLE */
    public void start(View view){
        startBtn.setVisibility(View.INVISIBLE);
        gameTransitionLayout.setVisibility(View.VISIBLE);
        // call the playAgain function to avoid redundancy
        playAgain(findViewById(R.id.scoreTextViewId)); // we can pass any view; it doesn't matter because we will not use it

    }

    /* ONCLICK LISTENER THAT WILL LISTEN TO EVERY CLICK OF THE ANSWER BUTTON */
    public void answer(View view) {

        // compare the location of correct answer to the tag of the clicked button
        if(Integer.toString(locationOfCorrectAnswer).equals(view.getTag())){
            remark.setText("Correct!");
            scoreCtr++;

        } else {
            remark.setText("Incorrect!");
        }

        numberOfQuestions++; // increase the number of questions regardless whether the ans is right or wrong
        scoreTextView.setText(Integer.toString(scoreCtr) + "/" + Integer.toString(numberOfQuestions)); // update the score in the textview
        generateQuestion(); // generate new question

    }

    /* DISABLE THE BUTTONS INSIDE THE GRIDLAYOUT */
    public void disableButtons() {
        // loop through them, if they are an instance of Button, disable it.
        for(View v : layoutButtons){
            if( v instanceof Button ) {
                ((Button)v).setEnabled(false);
            }
        }
    }
    /* ENABLE THE BUTTONS INSIDE THE GRIDLAYOUT */
    public void enableButtons() {
        // loop through them, if they are an instance of Button, disable it.
        for(View v : layoutButtons){
            if( v instanceof Button ) {
                ((Button)v).setEnabled(true);
            }
        }
    }

    /* FUNCTION FOR TIMER */
    public void timer(){
        timer = new CountDownTimer(20100, 1000) {
            @Override
            public void onTick(long l) {

                TextView timerTextView = (TextView) findViewById(R.id.timerTextViewId); // textview reference
                timerTextView.setText(l/1000 + "s"); // update the time in the timer textview
            }

            @Override
            public void onFinish() {
                disableButtons(); // disable buttons
                remark.setText("Done!");
                playAgainBtn.setVisibility(View.VISIBLE); // make the play btn visible when the game is finish
            }
        }.start();
    }


    /* FUNCTION THAT GENERATES NEW QUESTION */
    @SuppressLint("SetTextI18n")
    public void generateQuestion(){
        // display the generated number inside the question textview
        TextView question = (TextView) findViewById(R.id.questionTextViewId); // reference of the question textview

        // generates random number from 0 - 20
        int a = rand.nextInt(21);
        int b = rand.nextInt(21);

         /*
         generates random math operations
          0 => + (addition)
          1 => - (subtraction)
          2 => * (multiplication)
          3 => / (division)
         */
        int randomOperation = ThreadLocalRandom.current().nextInt(0,3);
        int op = 0;
        switch (randomOperation){
            case 0:
                op = 0;
                question.setText(a + " + " + b);
                break;
            case 1:
                op = 1;
                question.setText(a + " - " + b);
                break;
            case 2:
                op = 2;
                question.setText(a + " * " + b);
                break;
        }

        // call the checkAnswer function immediately
        changeAnswerText(a, b, op);
    }

    /* FUNCTION THAT CHANGES THE TEXT OF THE ANSWER BUTTON */
    public void changeAnswerText(int a, int b, int op){
        locationOfCorrectAnswer = rand.nextInt(4); // will hold the location of the correct answer; we pass 4 because we'll be picking from 0- 3

        // clear the array everytime that the array is called because it will just accumulate answers; if we wont clear the arraylist, only the first 4 elements will be shown in the button
        answers.clear();


        // fill out the array that will hold the answers using for loop
        for(int i=0; i<4; i++){ // this will run 4x because we have 4 buttons
            if (op == 0) {
                if (i == locationOfCorrectAnswer) {
                    answers.add(a + b); // add the correct answer in the array
                } else {
                    int wrongAnswer = rand.nextInt(41);
                    while (wrongAnswer == (a + b)) { // if wrong answer is the same as the right answer, continue to generate
                        wrongAnswer = rand.nextInt(201);
                    }
                    answers.add(wrongAnswer); // add the wrong answer in the array
                }
            } else if (op == 1){
                if (i == locationOfCorrectAnswer) {
                    answers.add(a - b); // add the correct answer in the array
                } else {
                    int wrongAnswer = rand.nextInt(41);
                    while (wrongAnswer == (a - b)) { // if wrong answer is the same as the right answer, continue to generate
                        wrongAnswer = rand.nextInt(41);
                    }
                    answers.add(wrongAnswer); // add the wrong answer in the array
                }
            } else if (op == 2){
                if (i == locationOfCorrectAnswer) {
                    answers.add(a * b); // add the correct answer in the array
                } else {
                    int wrongAnswer = rand.nextInt(41);
                    while (wrongAnswer == (a * b)) { // if wrong answer is the same as the right answer, continue to generate
                        wrongAnswer = rand.nextInt(41);
                    }
                    answers.add(wrongAnswer); // add the wrong answer in the array
                }
            }
        }

        // call the changeButtonAnswers to change the answers to each button
        changeButtonAnswers();


    }
    public void changeButtonAnswers(){
        /* Change the answers button text */
        // references for the answer button
        Button button0 = (Button) findViewById(R.id.btn1);
        Button button1 = (Button) findViewById(R.id.btn2);
        Button button2 = (Button) findViewById(R.id.btn3);
        Button button3 = (Button) findViewById(R.id.btn4);

        button0.setText(Integer.toString(answers.get(0)));
        button1.setText(Integer.toString(answers.get(1)));
        button2.setText(Integer.toString(answers.get(2)));
        button3.setText(Integer.toString(answers.get(3)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brain_trainer);

        startBtn = (Button) findViewById(R.id.startBtnId); // reference of the start button
        remark = (TextView) findViewById(R.id.remarkTextViewId); // reference for the remark textview
        scoreTextView = (TextView) findViewById(R.id.scoreTextViewId); // reference of the score textview
        playAgainBtn = (Button) findViewById(R.id.playAgainBtnId); // reference of the play again button
        gameTransitionLayout = (ConstraintLayout) findViewById(R.id.gameTransitionLayoutId); // reference of the constraint that will be use as pantakip
        gridLayout = findViewById(R.id.gridLayoutId); // reference to the grid layout that holds the buttons
        layoutButtons = gridLayout.getTouchables(); // reference to the arrayList of button views

        startBtn.setVisibility(View.VISIBLE); // make the start button visible at the starting process of the app
        gameTransitionLayout.setVisibility(View.INVISIBLE); // make the constraint that contains all the buttons, text views, and stuff invisible at the start of the app

    }
}