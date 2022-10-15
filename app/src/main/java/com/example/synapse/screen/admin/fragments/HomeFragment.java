package com.example.synapse.screen.admin.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.synapse.R;
import com.example.synapse.screen.util.PromptMessage;
import com.example.synapse.screen.util.readwrite.ReadWriteUserDetails;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // Global variables
    PromptMessage promptMessage = new PromptMessage();
    DatabaseReference totalUsersCarer;
    DatabaseReference totalUsersSenior;
    DatabaseReference totalBarangay;
    TextView tvTotalUsers, tvTotalBarangay;
    ArrayList barArraylist;
    int countSenior, countCarer;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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

    void displayTotalUsers(){
        Query query_senior = totalUsersSenior;
        Query query_carer = totalUsersCarer;
        query_senior.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int senior_count = (int) snapshot.getChildrenCount();
                    query_carer.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                           int carer_count = (int) snapshot.getChildrenCount();
                           int total_count = senior_count + carer_count;
                           String count = String.valueOf(total_count);
                           tvTotalUsers.setText(count);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            promptMessage.defaultErrorMessage(getActivity());
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }

    void displayTotalBarangay(){
        totalBarangay.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               int count_barangay = (int) snapshot.getChildrenCount();
               String count = String.valueOf(count_barangay);
               tvTotalBarangay.setText(count);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }

    void countUsers(String user, TextView textView){
        Query query = totalUsersCarer;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               int count_user = (int) snapshot.getChildrenCount();
               String count = String.valueOf(count_user);
               textView.setText(count);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }

    void countSenior(TextView textView){
        Query query = totalUsersSenior;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count_user = (int) snapshot.getChildrenCount();
                String count = String.valueOf(count_user);
                textView.setText(count);
                countSenior = count_user;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }

    void countCarer(TextView textView){
        Query query = totalUsersCarer;
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count_user = (int) snapshot.getChildrenCount();
                String count = String.valueOf(count_user);
                textView.setText(count);
                countCarer = count_user;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container,false);

       totalUsersCarer = FirebaseDatabase.getInstance().getReference("Users").child("Carers");
       totalUsersSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");
       totalBarangay = FirebaseDatabase.getInstance().getReference("Barangay");

       tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
       tvTotalBarangay = view.findViewById(R.id.tvTotalBarangay);
       TextView tvTotalSeniors = view.findViewById(R.id.tvTotalSeniors);
       TextView tvTotalCarers = view.findViewById(R.id.tvTotalCarers);

        displayTotalUsers();
        displayTotalBarangay();
       // countUsers("Senior",tvTotalSeniors);
       // countUsers("Carer",tvTotalCarers);


        countSenior(tvTotalSeniors);
        countCarer(tvTotalCarers);

        getData();

        BarChart barChart = view.findViewById(R.id.barChart);
        BarDataSet barDataSet = new BarDataSet(barArraylist, "Seniors and Carers");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.getDescription().setEnabled(true);

        return view;
    }

    void getData(){
        barArraylist = new ArrayList();
        barArraylist.add(new BarEntry(10f, 3));
        barArraylist.add(new BarEntry(14f, 2));
    }
}