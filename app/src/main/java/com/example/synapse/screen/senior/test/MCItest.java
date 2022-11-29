package com.example.synapse.screen.senior.test;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.example.synapse.screen.Login;
import com.example.synapse.screen.senior.RegisterSenior;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MCItest extends AppCompatActivity implements View.OnClickListener {

    int currentProgress = 0;
    ProgressBar progressBar;

    TextView totalQuestionsTextView;
    TextView questionTextView;
    TextView countQuestion;
    Button ansA, ansB, ansC, ansD;
    Button submitBtn;

    String msg = "The Senior’s AD8 range fall to 2 or Higher.\n" +
            "\n" +
            "We regret that we are unable to process your registration. The application is designed for older adults with mild cognitive impairment. It is recommended to consult a primary care physician if scoring results indicates potential dementia. \n" +
            "\n" +
            "PLEASE NOTE: \n" +
            "The AD8 diagnostic test cannot diagnose dementing disorders. Nevertheless, the test effectively detects the onset of many common dementias at an early stage of the disease.\n" +
            "(Validation of AD8-Philippines (AD8-P): A Brief Informant-Based Questionnaire for Dementia Screening in the Philippines)";

    int score = 0; // total score
    int currentQuestionIndex = 0;
    int ctr = 0;

    int totalQuestion = 7; // number of questions to be generated in front end (4 questions only)
    String selectedAnswer = ""; // user selected answer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mcitest);

        // id reference for the views
        totalQuestionsTextView = findViewById(R.id.total_question);
        questionTextView = findViewById(R.id.question);
        countQuestion = findViewById(R.id.countQuestion);
        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        ansC = findViewById(R.id.ans_C);
        ansD = findViewById(R.id.ans_D);
        progressBar = findViewById(R.id.progressBar);
        submitBtn = findViewById(R.id.submit_btn);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        progressBar.setProgress(currentProgress);

        loadNewQuestion();
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

            currentProgress += 1;
            progressBar.setProgress(currentProgress);
            progressBar.setMax(7);
            countQuestion.setText(currentProgress + " of 7");

            if(selectedAnswer.equals(QuestionAnswer.correctAnswers[currentQuestionIndex])){
                score++;
            }
            currentQuestionIndex++;
            loadNewQuestion();
        }else{
            //choices button clicked
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(getResources().getColor(R.color.mid_violet));
            submitBtn.setEnabled(true);
        }

    }

    void loadNewQuestion(){
        Log.d("COUNTER", String.valueOf(ctr)); // print current question counter
        submitBtn.setEnabled(false);
        if(ctr == totalQuestion){
            finishQuiz();
            return;
        }

        Log.d("currentQuestionIndex inside loadNewQuestion()", String.valueOf(currentQuestionIndex));
        questionTextView.setText(QuestionAnswer.question[currentQuestionIndex]);
        ansA.setText(QuestionAnswer.choices[currentQuestionIndex][0]);
        ansB.setText(QuestionAnswer.choices[currentQuestionIndex][1]);
        ansC.setText(QuestionAnswer.choices[currentQuestionIndex][2]);
    }

    void finishQuiz(){
        if(score <= 1){
            new AlertDialog.Builder(this)
                    .setTitle("Congratulations!")
                    .setMessage("You have passed the assessment. Carer can now register your account.\"")
                    .setPositiveButton("PROCEED",(dialogInterface, i) -> checkCarerEmail())
                    .setCancelable(false)
                    .show();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("The AD8 score equates to the following: " +
                            "\n 0 - 1:\tNormal/Mild Cognition Impairment \n2 or Higher: Potential Dementia")
                    .setMessage(msg)
                    .setPositiveButton("I understand", (dialogInterface, i) -> exit())
                    .setCancelable(true)
                    .show();
        }
    }

    void checkCarerEmail(){
        // go to registration page
        progressBar.setProgress(0);
        startActivity(new Intent(this, CheckCarerEmail.class));
        finish();
    }

    void exit(){
        // exit the app
        progressBar.setProgress(0);
        startActivity(new Intent(this, Login.class));
        finish();
    }
}