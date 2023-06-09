package com.example.synapse.screen.senior.test;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.example.synapse.screen.Login;
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

    int currentProgress = 2;
    ProgressBar progressBar;

    TextView totalQuestionsTextView;
    TextView questionTextView;
    TextView countQuestion;
    Button ansA, ansB, ansC;
    Button submitBtn;

    String msg = "The Senior’s AD8 range fall to 2 or higher.\n" +
            "\n" +
            "We regret that we are unable to process your registration. The application is designed for older adults with mild cognitive impairment. It is recommended to consult a primary care physician if scoring results indicates potential dementia. \n" +
            "\n" +
            "PLEASE NOTE: \n" +
            "The AD8 diagnostic test cannot diagnose dementing disorders. Nevertheless, the test effectively detects the onset of many common dementias at an early stage of the disease.\n" +
            "Galvin et al., 2005. The AD8: a brief informant interview to detect dementia. Neurology, 65(4), 559-564 \n\n" +
            "Tool as tested in the Philippine context:\n\n" +
            "Dominguez et al., 2021. Validation of AD8-Philippines (AD8-P): A Brief Informant-Based Questionnaire for Dementia Screening in the Philippines. International Journal of Alzheimer's Disease";

    int score = 0; // total score
    int currentQuestionIndex = 0;
    int ctr = 0;

    int totalQuestion = 8; // number of questions to be generated in front end (4 questions only)
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
        progressBar = findViewById(R.id.progressBar);
        submitBtn = findViewById(R.id.submit_btn);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        countQuestion.setText("1 of 8");

        progressBar.setProgress(1);
//        currentProgress++;
        loadNewQuestion();
    }

    @Override
    public void onClick(View view) {
        ansA.setBackgroundColor(Color.WHITE);
        ansB.setBackgroundColor(Color.WHITE);
        ansC.setBackgroundColor(Color.WHITE);

        Button clickedButton = (Button) view;
        if(clickedButton.getId() == R.id.submit_btn){
            ++ctr; // increase question counter

            progressBar.setProgress(currentProgress);
            progressBar.setMax(7);
            if(currentProgress == 9){
                currentProgress = 8;
            }
            countQuestion.setText(currentProgress + " of 8");
            currentProgress += 1;
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
                    .setTitle("The senior's total AD8 score is: " + score )
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
        // exit he app
        progressBar.setProgress(0);
        startActivity(new Intent(this, Login.class));
        finish();
    }
}