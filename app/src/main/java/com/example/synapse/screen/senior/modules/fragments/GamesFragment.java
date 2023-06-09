package com.example.synapse.screen.senior.modules.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.synapse.R;
import com.example.synapse.screen.senior.SeniorMainActivity;
import com.example.synapse.screen.senior.games.MathGame;
import com.example.synapse.screen.senior.games.TriviaQuiz;
import com.example.synapse.screen.senior.modules.view.TicTacToeHome;
import com.google.android.material.card.MaterialCardView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GamesFragment extends Fragment {

    // Global variables
    MaterialCardView btnMath,btnTicTacToe,btnTriviaQuiz;
    ImageButton ibBack;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GamesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GamesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GamesFragment newInstance(String param1, String param2) {
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_senior_games, container, false);
        ibBack = view.findViewById(R.id.ibBack);
        btnMath = view.findViewById(R.id.btnMath);
        btnTicTacToe = view.findViewById(R.id.btnTicTacToe);
        btnTriviaQuiz = view.findViewById(R.id.btnTriviaQuiz);

        // redirect user to math screen
        btnMath.setOnClickListener(v -> startActivity(new Intent(getActivity(), MathGame.class)));

        // redirect user to tic tac toe screen
        btnTicTacToe.setOnClickListener(v -> startActivity(new Intent(getActivity(), TicTacToeHome.class)));

        // redirect user to trivia quiz screen
        btnTriviaQuiz.setOnClickListener(v -> startActivity(new Intent(getActivity(), TriviaQuiz.class)));

        // redirect user to senior home scree
        ibBack.setOnClickListener(v -> startActivity(new Intent(getActivity(), SeniorMainActivity.class)));

        return view;
    }
}