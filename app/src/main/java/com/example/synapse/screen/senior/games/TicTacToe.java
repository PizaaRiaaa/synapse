package com.example.synapse.screen.senior.games;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerMainActivity;
import com.example.synapse.screen.senior.SeniorMainActivity;
import com.example.synapse.screen.senior.modules.fragments.GamesFragment;
import com.example.synapse.screen.util.AuditTrail;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

public class TicTacToe extends AppCompatActivity {

    // Global variables
    int activePlayer = 0; // this integer will serve as a flag (boolean); 0 - Yellow, 1 - Red
    Dialog dialog;
    String winner;

    TextView tvCongrats;
    ImageView ivPlayer;
    ImageButton btnExit;

    MediaPlayer bgMusic;

    AuditTrail auditTrail = new AuditTrail();
    DatabaseReference referenceSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");
    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    // initially, the state of the game is none. Empty represents by 2, so in our array, let's put nine 2s
    // 0 - Yellow, 1 - Red, 2 - Empty
    int[] gameState = {2,2,2,2,2,2,2,2,2};

    // create an array of winning positions
    int [][] winningPositions = {
            {0,1,2},{3,4,5},{6,7,8}, // horizontal winning positions
            {0,3,6},{1,4,7},{2,5,8}, // vertical winning positions
            {0,4,8}, // diagonal right winning position
            {2,4,6} // diagonal left winning position
    };

    // checks whether the game is still going or already done
    boolean gameActive = true;

    /* onClick functions for ImageView */
   public void dropIn(View view){ // need a View parameter which is the ImageView that was tapped on

       ImageView counter = (ImageView) view; // actual imageview that was tapped on

       // play sound effect
       playSound(R.raw.pop_sound);

       // check the tag
        int tappedCounterTag = Integer.parseInt(counter.getTag().toString());

        // check if the gameState element in the array is already occupied by 0 or 1 and if the gameActive is true
        if(gameState[tappedCounterTag] == 2 && gameActive){
            // game state tracker - change the gameState array every tapped using the imageview tag
            gameState[tappedCounterTag] = activePlayer;

            // if gameState array doesn't contain any 2, no one won
            Log.i("Array", "Array " + Arrays.toString(gameState));
            if(gameState[0]!= 2 && gameState[1]!= 2 && gameState[2]!= 2
                    && gameState[3]!= 2 && gameState[4]!= 2 && gameState[5]!= 2
                    && gameState[6]!= 2 && gameState[7]!= 2 && gameState[8]!= 2){

                // display no one won
                displayNoOneWon();
            }

            // animate the counter
            counter.setTranslationY(-1000); // take it off at the top of the screen; san manggagaling
            // change the image per click
            if(activePlayer == 0){
                activePlayer = 1;
                counter.setImageResource(R.drawable.ic_zero); // set an image on the imageview
            } else {
                activePlayer = 0;
                counter.setImageResource(R.drawable.ic_ex); // set an image on the imageview
            }
            counter.animate().translationYBy(1000).rotation(1000).setDuration(1000); // paano bababa

            /* CHECK AGAINST THE WINNING POSITION */
            for(int[] winningPosition: winningPositions){
                if((gameState[winningPosition[0]]) == gameState[winningPosition[1]] &&
                        gameState[winningPosition[1]] == gameState[winningPosition[2]] &&
                        gameState[winningPosition[0]] != 2) {

                    // if all conditions are met, gameActive will be false
                    gameActive = false;

                    // decide which color has won
                    if(activePlayer == 1){
                        winner = "Yellow";
                    } else {
                        winner = "Blue";
                    }
                    // if all the above condition is all met, then someone has won
                    displayGameStatus();
                    // play sound effect
                    playSound(R.raw.win_sound);
                }
            }
        }
    }

    /* onClick function for Retry Button */
    public void playAgain(){

        auditTrail.auditTrail(
                "Finished Playing Tic-tac-toe",
                "Tic-tac-toe",
                "Game", "Senior", referenceSenior, mUser);

        // remove all the imageview if retry button is clicked
        androidx.gridlayout.widget.GridLayout boardGridLayout = findViewById(R.id.boardGridLayoutId);
        for(int i = 0; i < boardGridLayout.getChildCount(); i++) {

            ImageView counter = (ImageView) boardGridLayout.getChildAt(i); // child
            counter.setImageDrawable(null); // remove the all the image that has been set before
        }

        // update the player variables
        activePlayer = 0;
        gameState = new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2};
        gameActive = true;
    }

    void displayGameStatus(){
        dialog.show();
        ivPlayer = dialog.findViewById(R.id.ivPlayer);
        if(winner.equals("Blue")){
           ivPlayer.setBackgroundResource(R.drawable.ic_ex);
        }else{
            ivPlayer.setBackgroundResource(R.drawable.ic_zero);
        }
        tvCongrats = dialog.findViewById(R.id.tvCongrats);
        tvCongrats.setText("Congratulations! \n" + winner + " has won!");
    }

    void displayNoOneWon(){
        dialog.show();
        ivPlayer = dialog.findViewById(R.id.ivPlayer);
        tvCongrats = dialog.findViewById(R.id.tvCongrats);
        tvCongrats.setText("No one won :(");
        ivPlayer.setBackgroundResource(R.drawable.ic_no_one_won);
    }

    void playSound(int sound){
        MediaPlayer mp = MediaPlayer.create(this, sound);
        mp.start();
    }

    // check if back button was pressed
    @Override
    public void onBackPressed(){
        super.onBackPressed();

        bgMusic.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        dialog = new Dialog(TicTacToe.this);
        dialog.setContentView(R.layout.custom_dialog_tic_tac_toe);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background3));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation1;

        btnExit = findViewById(R.id.btnExit);

        bgMusic = MediaPlayer.create(this, R.raw.tic_tac_toe);
        bgMusic.start();
        bgMusic.setLooping(true);

        MaterialButton btnRetry = dialog.findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(v -> {
            dialog.dismiss();
            playAgain();
        });

        btnExit.setOnClickListener(v -> {
            bgMusic.stop();
            finish();
        });

        MaterialButton btnQuit = dialog.findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener(v -> {
            bgMusic.stop();
            finish();
        });

    }

}