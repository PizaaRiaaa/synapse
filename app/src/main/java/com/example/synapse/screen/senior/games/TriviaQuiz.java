package com.example.synapse.screen.senior.games;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.google.android.material.button.MaterialButton;

import android.app.AlertDialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.ThreadLocalRandom;

public class TriviaQuiz extends AppCompatActivity implements View.OnClickListener {

    TextView totalQuestionsTextView;
    TextView questionTextView;
    ImageView btnExit;

    MaterialButton ansA, ansB, ansC, ansD;
    MaterialButton submitBtn;
    MediaPlayer bgMusic;
    MediaPlayer bgCorrect;



    int score = 0; // total score
    int currentQuestionIndex = ThreadLocalRandom.current().nextInt(0,76);
    int ctr = 0;

    int totalQuestion = 10; // number of questions to be generated in front end (4 questions only)
    String selectedAnswer = ""; // user selected answer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_quiz);

        // id reference for the views
        totalQuestionsTextView = findViewById(R.id.total_question);
        questionTextView = findViewById(R.id.question);
        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        ansC = findViewById(R.id.ans_C);
        ansD = findViewById(R.id.ans_D);
        btnExit = findViewById(R.id.btnExit);
        submitBtn = findViewById(R.id.submit_btn);

        bgCorrect = MediaPlayer.create(this, R.raw.correct);
        bgCorrect.setLooping(false);
        bgMusic = MediaPlayer.create(this, R.raw.trivia);
        bgMusic.start();
        bgMusic.setLooping(true);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        btnExit.setOnClickListener(v -> {
            bgMusic.stop();
            finish();
        });

        loadNewQuestion();
    }

    // check if back button was pressed
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        bgMusic.stop();
    }


    @Override
    public void onClick(View view) {

        ansA.setBackgroundColor(Color.WHITE);
        ansB.setBackgroundColor(Color.WHITE);
        ansC.setBackgroundColor(Color.WHITE);
        ansD.setBackgroundColor(Color.WHITE);

        Button clickedButton = (Button) view;
        if(clickedButton.getId() == R.id.submit_btn){
            ++ctr; // increase question counter
            Log.d("currentQuestionIndex inside onClick()", String.valueOf(currentQuestionIndex));

            if(selectedAnswer.equals(QuestionAnswer.correctAnswers[currentQuestionIndex])){
                bgCorrect.start();
                score++;
            }
//            currentQuestionIndex++;
            loadNewQuestion();


        }else{
            //choices button clicked
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(getResources().getColor(R.color.light_yellow));
        }

    }
    void loadNewQuestion(){
        currentQuestionIndex = ThreadLocalRandom.current().nextInt(0,76); // currentQuestionIndex
        Log.d("COUNTER", String.valueOf(ctr)); // print current question counter


        if(ctr == totalQuestion){
            finishQuiz();
            return;
        }

        Log.d("currentQuestionIndex inside loadNewQuestion()", String.valueOf(currentQuestionIndex));

        questionTextView.setText(QuestionAnswer.question[currentQuestionIndex]);
        ansA.setText(QuestionAnswer.choices[currentQuestionIndex][0]);
        ansB.setText(QuestionAnswer.choices[currentQuestionIndex][1]);
        ansC.setText(QuestionAnswer.choices[currentQuestionIndex][2]);
        ansD.setText(QuestionAnswer.choices[currentQuestionIndex][3]);

    }
    void finishQuiz(){
        String passStatus = "";
        if(score > totalQuestion * 0.60){
            passStatus = "Passed";
        }else{
            passStatus = "Failed";
        }

        new AlertDialog.Builder(this)
                .setTitle(passStatus)
                .setMessage("Score is " + score+" out of " + totalQuestion)
                .setPositiveButton("Restart",(dialogInterface, i) -> restartQuiz())
                .setNegativeButton("Quit",(dialogInterface, i) -> quitGame())
                .setCancelable(false)
                .show();
    }
    void restartQuiz(){
        ctr = 0;
        score = 0;
        currentQuestionIndex = ThreadLocalRandom.current().nextInt(0,76);
        loadNewQuestion();
    }

    void quitGame(){
        bgMusic.stop();
        finish();
    }
}