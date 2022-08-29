package com.example.synapse.screen.senior.games;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ThreadLocalRandom;

public class TriviaQuiz extends AppCompatActivity implements View.OnClickListener {

    TextView totalQuestionsTextView;
    TextView questionTextView;
    Button ansA, ansB, ansC, ansD;
    Button submitBtn;

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
        submitBtn = findViewById(R.id.submit_btn);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

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
            Log.d("currentQuestionIndex inside onClick()", String.valueOf(currentQuestionIndex));

            if(selectedAnswer.equals(QuestionAnswer.correctAnswers[currentQuestionIndex])){
                score++;
            }
//            currentQuestionIndex++;
            loadNewQuestion();


        }else{
            //choices button clicked
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(Color.MAGENTA);
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
                .setCancelable(false)
                .show();
    }
    void restartQuiz(){
        ctr = 0;
        score = 0;
        currentQuestionIndex = ThreadLocalRandom.current().nextInt(0,76);
        loadNewQuestion();
    }
}