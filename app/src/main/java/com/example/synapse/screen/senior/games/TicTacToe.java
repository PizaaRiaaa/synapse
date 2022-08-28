package com.example.synapse.screen.senior.games;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

public class TicTacToe extends AppCompatActivity {

    int activePlayer = 0; // this integer will serve as a flag (boolean); 0 - Yellow, 1 - Red

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

                // reference for the winner textview and retry button
                TextView winnerTV = (TextView) findViewById(R.id.winnerTextViewId);
                Button retryBtn = (Button) findViewById(R.id.retryBtnId );

                winnerTV.setText("No one won :( ");
                winnerTV.setVisibility(View.VISIBLE);
                retryBtn.setVisibility(View.VISIBLE);
            }

            // animate the counter
            counter.setTranslationY(-1000); // take it off at the top of the screen; san manggagaling
            // change the image per click
            if(activePlayer == 0){
                activePlayer = 1;
                counter.setImageResource(R.drawable.ic_yellow); // set an image on the imageview
            } else {
                activePlayer = 0;
                counter.setImageResource(R.drawable.ic_red); // set an image on the imageview
            }
            counter.animate().translationYBy(1000).rotation(500).setDuration(500); // paano bababa


            /* CHECK AGAINST THE WINNING POSITION */
            for(int[] winningPosition: winningPositions){
                if((gameState[winningPosition[0]]) == gameState[winningPosition[1]] &&
                        gameState[winningPosition[1]] == gameState[winningPosition[2]] &&
                        gameState[winningPosition[0]] != 2) {

                    // if all conditions are met, gameActive will be false
                    gameActive = false;

                    // decide which color has won
                    String winner;
                    if(activePlayer == 1){
                        winner = "Yellow";
                    } else {
                        winner = "Red";
                    }

                    // if all the above condition is all met, then someone has won
                    // reference for the winner textview and retry button
                    TextView winnerTV = (TextView) findViewById(R.id.winnerTextViewId);
                    Button retryBtn = (Button) findViewById(R.id.retryBtnId );

                    winnerTV.setText("Congratulations! " + winner + " has won!");
                    winnerTV.setVisibility(View.VISIBLE);
                    retryBtn.setVisibility(View.VISIBLE);

                }
            }

        }
    }

    /* onClick function for Retry Button */
    public void playAgain(View view){

        // reference to the button and textview
        TextView winnerTV = (TextView) findViewById(R.id.winnerTextViewId);
        Button retryBtn = (Button) findViewById(R.id.retryBtnId );

        // make textview and button invisible again
        winnerTV.setVisibility(View.INVISIBLE);
        retryBtn.setVisibility(View.INVISIBLE);

        // remove all the imageview if retry button is clicked
        GridLayout boardGridLayout = (GridLayout) findViewById(R.id.boardGridLayoutId);
        for(int i = 0; i < boardGridLayout.getChildCount(); i++) {

            ImageView counter = (ImageView) boardGridLayout.getChildAt(i); // child
            counter.setImageDrawable(null); // remove the all the image that has been set before
        }

        // update the player variables
        activePlayer = 0;
        gameState = new int[]{2, 2, 2, 2, 2, 2, 2, 2, 2};
        gameActive = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);
    }

}