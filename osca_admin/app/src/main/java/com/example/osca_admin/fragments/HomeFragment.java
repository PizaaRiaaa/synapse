package com.example.osca_admin.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.osca_admin.R;
import com.example.osca_admin.util.PromptMessage;
import com.example.osca_admin.util.ReplaceFragment;
import com.example.osca_admin.util.readwrite.ReadWriteSenior;
import com.example.osca_admin.util.readwrite.ReadWriteTotalReminders;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // Global variables
    private PromptMessage promptMessage = new PromptMessage();

    private DatabaseReference totalUsersCarer;
    private DatabaseReference totalUsersSenior;
    private DatabaseReference totalBarangay;
    private DatabaseReference referenceSenior;
    private DatabaseReference referenceCarer;
    private DatabaseReference referenceTotalReminders;

    private TextView tvSeniorPlus;
    private TextView tvCarerPlus;
    PieChart pieChart;
    private ImageView ivSenior1;
    private ImageView ivSenior2;
    private ImageView ivSenior3;
    private ImageView ivSenior4;
    private ImageView ivCarer1;
    private ImageView ivCarer2;
    private ImageView ivCarer3;
    private ImageView ivCarer4;

    ArrayList barArraylist;

    float medicine;
    float physical;
    float appointment;
    float games;


    int countSenior, countCarer;

    public int countMedication;
    public int countPhysicalActivity;
    public int countAppointment;
    public int countGames;

    final String carer1 = "QcStU9l9IZRWtwnsBnLTnJhHpDI3";
    final String carer2 = "JiKpzYb4vbh98DOWUK1KL4kSj7Q2";
    final String carer3 = "BwK0eiO6eLh7Y0ViKhPlgpUPIma2";
    final String carer4 = "QMMVff8xaET7tAbdaeCvsgOC9Rb2";

    final String senior1 = "87GnwUND6pa39UqG0I1RhpKMIAY2";
    final String senior2 = "F0zDiUp4dvTZdENqbGebspxMqBv1";
    final String senior3 = "KlCXZM4g7nPGR2sLK0Ar7K5QI1o2";
    final String senior4 = "HnjVW8xEU2Xw1p3ESQ4soiCv6sg2";


    TextView tvTotalUsers, tvTotalBarangay;

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
        totalBarangay.child("barangay").addListenerForSingleValueEvent(new ValueEventListener() {
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

    void countMedicine(){
        DatabaseReference medication = FirebaseDatabase.getInstance().getReference("Medication Reminders");
        medication.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countMedication = (int) snapshot.getChildrenCount();
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("medication", countMedication);

                referenceTotalReminders.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("medicine","successfully stored count");

                    }
                });
            }

            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    void countPhysical(){
        DatabaseReference physical = FirebaseDatabase.getInstance().getReference("Physical Activity Reminders");
        physical.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countPhysicalActivity = (int) snapshot.getChildrenCount();
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("physical", countPhysicalActivity);

                referenceTotalReminders.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("physical","successfully stored count");

                    }
                });
            }

            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void countAppointment(){
        DatabaseReference appointment = FirebaseDatabase.getInstance().getReference("Appointment Reminders");
        appointment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countAppointment = (int) snapshot.getChildrenCount();
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("appointment", countAppointment);

                referenceTotalReminders.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("appointment","successfully stored count");

                    }
                });
            }

            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void countGames(){
        DatabaseReference games = FirebaseDatabase.getInstance().getReference("Games Reminders");
        games.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countGames = (int) snapshot.getChildrenCount();
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("games", countAppointment);

                referenceTotalReminders.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("games","successfully stored count");

                    }
                });
            }

            public void onCancelled(@NonNull DatabaseError error) {

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
                returnCount(count, tvSeniorPlus);
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
                returnCount(count, tvCarerPlus);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }

   void returnCount(String count, TextView tv){
        tv.setText(count + "+");
   }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    void displayOverlayImageSenior(String userID, DatabaseReference db, ImageView iv){
        db.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   ReadWriteSenior rw = snapshot.getValue(ReadWriteSenior.class);
                   String imageURL = rw.getImageURL();
                   Picasso.get()
                           .load(imageURL)
                           .transform(new CropCircleTransformation())
                           .into(iv);
               }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }

    void displayOverlayImageCarer(String userID, DatabaseReference db, ImageView iv){
        db.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ReadWriteSenior rw = snapshot.getValue(ReadWriteSenior.class);
                    String imageURL = rw.getImageURL();
                    Picasso.get()
                            .load(imageURL)
                            .transform(new CropCircleTransformation())
                            .into(iv);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }

  //  void getRemindersData(){
  //      referenceTotalReminders.addValueEventListener(new ValueEventListener() {
  //          @Override
  //          public void onDataChange(@NonNull DataSnapshot snapshot) {
  //              if(snapshot.hasChild("appointment")){
  //                  ReadWriteTotalReminders rw = snapshot.getValue(ReadWriteTotalReminders.class);
  //                  medicine = rw.getMedication();

  //              }

  //                           //physical = rw.getPhysical();
  //                           //appointment = rw.getAppointment();
  //                           //games = rw.getGames();
  //          }

  //          @Override
  //          public void onCancelled(@NonNull DatabaseError error) {

  //          }
  //      });
//}

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

        // force app to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container,false);

        ReplaceFragment replaceFragment = new ReplaceFragment();

        totalUsersCarer = FirebaseDatabase.getInstance().getReference("Users").child("Carers");
        totalUsersSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");
        totalBarangay = FirebaseDatabase.getInstance().getReference("MandaluyongBarangay");

        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);
        tvTotalBarangay = view.findViewById(R.id.tvTotalBarangay);

        ivSenior1 = view.findViewById(R.id.ivSenior1);
        ivSenior2 = view.findViewById(R.id.ivSenior2);
        ivSenior3 = view.findViewById(R.id.ivSenior3);
        ivSenior4 = view.findViewById(R.id.ivSenior4);

        ivCarer1 = view.findViewById(R.id.ivCarer1);
        ivCarer2 = view.findViewById(R.id.ivCarer2);
        ivCarer3 = view.findViewById(R.id.ivCarer3);
        ivCarer4 = view.findViewById(R.id.ivCarer4);

        TextView tvTotalSeniors = view.findViewById(R.id.tvTotalSeniors);
        TextView tvTotalCarers = view.findViewById(R.id.tvTotalCarers);
        TextView tvTeam = view.findViewById(R.id.tvTeam);
        TextView tvCarers = view.findViewById(R.id.tvCarersTab);
        TextView tvSeniors = view.findViewById(R.id.tvSeniors);
        MaterialCardView btnAddBarangay = view.findViewById(R.id.btnAddBarangay);
        MaterialCardView btnEditBarangay = view.findViewById(R.id.ibEdit);
        MaterialCardView cardSeniors = view.findViewById(R.id.cardSenior);
        MaterialCardView cardCarers = view.findViewById(R.id.cardCarers);
        pieChart = view.findViewById(R.id.piechart);
        PieChart pieChart1 = view.findViewById(R.id.piechartSenior);
        tvSeniorPlus = view.findViewById(R.id.tvSeniorPlus);
        tvCarerPlus = view.findViewById(R.id.tvCarerPlus);

        referenceCarer = FirebaseDatabase.getInstance().getReference("Users").child("Carers");
        referenceSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");
        referenceTotalReminders = FirebaseDatabase.getInstance().getReference("TotalRemindersSummary");

        displayOverlayImageSenior(senior1, referenceSenior, ivSenior1);
        displayOverlayImageSenior(senior2, referenceSenior, ivSenior2);
        displayOverlayImageSenior(senior3, referenceSenior, ivSenior3);
        displayOverlayImageSenior(senior4, referenceSenior, ivSenior4);

        displayOverlayImageCarer(carer1, referenceCarer, ivCarer1);
        displayOverlayImageCarer(carer2, referenceCarer, ivCarer2);
        displayOverlayImageCarer(carer3, referenceCarer, ivCarer3);
        displayOverlayImageCarer(carer4, referenceCarer, ivCarer4);

        displayTotalUsers();
        displayTotalBarangay();

        //countMedicine();
        //countPhysical();
        //countAppointment();
        //countGames();

        btnAddBarangay.setOnClickListener(v -> replaceFragment.replaceFragment(new AddBarangayFragment(), getActivity()));
        btnEditBarangay.setOnClickListener(v -> replaceFragment.replaceFragment(new EditBarangayFragment(), getActivity()));
        tvTeam.setOnClickListener(v -> replaceFragment.replaceFragment(new TeamFragment(), getActivity()));
        tvCarers.setOnClickListener(v -> replaceFragment.replaceFragment(new CarersFragment(), getActivity()));
        tvSeniors.setOnClickListener(v -> replaceFragment.replaceFragment(new SeniorsFragment(), getActivity()));
        cardSeniors.setOnClickListener(v -> replaceFragment.replaceFragment(new SeniorsFragment(), getActivity()));
        cardCarers.setOnClickListener(v -> replaceFragment.replaceFragment(new CarersFragment(), getActivity()));

        countSenior(tvTotalSeniors);
        countCarer(tvTotalCarers);

        TextClock currentTime = view.findViewById(R.id.tcTime);
        currentTime.setFormat12Hour("hh:mm a");

       // barArraylist = new ArrayList();
       // barArraylist.add(new BarEntry(1,3 ));
       // barArraylist.add(new BarEntry(2, 2));

       // BarChart barChart = getView().findViewById(R.id.barChart);
       // BarDataSet barDataSet = new BarDataSet(barArraylist, "Total Seniors in Barangay");
       // BarData barData = new BarData(barDataSet);
       // barChart.setData(barData);
       // barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
       // barDataSet.setValueTextColor(Color.BLACK);
       // barDataSet.setValueTextSize(16f);
       // barChart.getDescription().setEnabled(true);

      // countMedicine();
      // countPhysical();
      // countAppointment();
      // countGames();

      // getRemindersData();
        ArrayList<PieEntry> reminders1 = new ArrayList<>();
        PieDataSet pieDataSet1 = new PieDataSet(reminders1, "Seniors");
        reminders1.add(new PieEntry(  5, "Addition Hills"));
        reminders1.add(new PieEntry( 11, "Bagong Silang"));
        reminders1.add(new PieEntry( 3, "Barangka Drive"));
        reminders1.add(new PieEntry( 9, "Hulo"));

        pieDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet1.setValueTextColor(Color.BLACK);
        pieDataSet1.setValueTextSize(16f);

        PieData pieData1 = new PieData(pieDataSet1);

        pieChart1.setData(pieData1);
        pieChart1.getDescription().setEnabled(false);
        pieChart1.setCenterText("Total Seniors in Barangay");
        pieChart1.animate();

        //

        ArrayList<PieEntry> reminders = new ArrayList<>();
        PieDataSet pieDataSet = new PieDataSet(reminders, "Reminders");
        reminders.add(new PieEntry(  20, "Medication"));
        reminders.add(new PieEntry( 16, "Physical Activity"));
        reminders.add(new PieEntry( 11, "Appointment"));
        reminders.add(new PieEntry( 5, "Games"));

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Total Reminders Per Module");
        pieChart.animate();

        return view;
    }
}