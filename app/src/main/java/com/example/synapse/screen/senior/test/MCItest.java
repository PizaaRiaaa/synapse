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

    int score = 0; // total score
    int currentQuestionIndex = 0;
    int ctr = 0;

    int totalQuestion = 5; // number of questions to be generated in front end (4 questions only)
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
            progressBar.setMax(5);
            countQuestion.setText(currentProgress + " of 5");

            if(selectedAnswer.equals(QuestionAnswer.correctAnswers[currentQuestionIndex])){
                score++;
            }
            currentQuestionIndex++;
            loadNewQuestion();
        }else{
            //choices button clicked
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(getResources().getColor(R.color.mid_violet));
        }

    }

    void loadNewQuestion(){
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
        if(score > totalQuestion * 0.60){
            new AlertDialog.Builder(this)
                    .setTitle("Congratulations!")
                    .setMessage("You have passed the assessment. You can now register to our application.\"")
                    .setPositiveButton("Register",(dialogInterface, i) -> register())
                    .setCancelable(false)
                    .show();
        }else{
            new AlertDialog.Builder(this)
                    .setTitle("Sorry...")
                    .setMessage("You are not allowed to register. For more information, click this.")
                    .setPositiveButton("I understand", (dialogInterface, i) -> exit())
                    .setCancelable(true)
                    .show();
        }
    }

    void register(){
        // go to registration page
        progressBar.setProgress(0);
        startActivity(new Intent(this, RegisterSenior.class));
        finish();
    }

    void exit(){
        // exit the app
        progressBar.setProgress(0);
        startActivity(new Intent(this, Login.class));
        finish();
    }
}