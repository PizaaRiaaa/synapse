package com.example.osca_admin.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.osca_admin.R;
import com.example.osca_admin.util.AuditTrail;
import com.example.osca_admin.util.PromptMessage;
import com.example.osca_admin.util.ReplaceFragment;
import com.example.osca_admin.util.readwrite.ReadWriteCarer;
import com.example.osca_admin.util.readwrite.ReadWriteSenior;
import com.example.osca_admin.util.viewholder.CarerViewHolder;
import com.example.osca_admin.util.viewholder.SeniorViewHolder;
import com.example.osca_admin.views.SeniorInfoActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SeniorsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SeniorsFragment extends Fragment {

    DatabaseReference referenceSenior;
    DatabaseReference referenceAdmin;
    RecyclerView recyclerView;

    ReplaceFragment replaceFragment = new ReplaceFragment();
    PromptMessage promptMessage = new PromptMessage();
    AuditTrail auditTrail = new AuditTrail();

    TextView tvCountSeniors;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SeniorsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SeniorsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SeniorsFragment newInstance(String param1, String param2) {
        SeniorsFragment fragment = new SeniorsFragment();
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

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    void displayTotalCarers(){
        referenceSenior.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count_admin = (int) snapshot.getChildrenCount();
                String count = String.valueOf(count_admin);
                tvCountSeniors.setText(count + " seniors");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                promptMessage.defaultErrorMessage(getActivity());
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // layout for recycle view
        recyclerView = view.findViewById(R.id.recyclerview_seniors);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    void displaySeniors(String lastname) {
        referenceSenior.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ignored : snapshot.getChildren()) {
                    Query query = snapshot.getRef().orderByChild("lastName").startAt(lastname).endAt(lastname+"\uf8ff");
                    FirebaseRecyclerOptions<ReadWriteSenior> options = new FirebaseRecyclerOptions.Builder<ReadWriteSenior>().setQuery(query, ReadWriteSenior.class).build();
                    FirebaseRecyclerAdapter<ReadWriteSenior, SeniorViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteSenior, SeniorViewHolder>(options) {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @SuppressLint("SetTextI18n")
                        @Override
                        protected void onBindViewHolder(@NonNull SeniorViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteSenior model) {

                            String id = getRef(position).getKey();
                            holder.id.setText("ID: " + id);
                            holder.email.setText("email: " + model.getEmail());
                            holder.fullName.setText("full name: " + model.getFirstName() + " " + model.lastName);
                            holder.address.setText("addr: " + model.getAddress());

                            Picasso.get()
                                    .load(model.getImageURL())
                                    .transform(new CropCircleTransformation())
                                    .into(holder.ivPicture);

                            displayTotalCarers();

                             holder.itemView.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {

                                     auditTrail.auditTrail("Viewed Senior Profile",
                                             model.firstName + " " + model.lastName,
                                             "Admin", "Admin", referenceAdmin,
                                             getDefaults("userKey", getActivity()));

                                     Intent intent = new Intent(getActivity(), SeniorInfoActivity.class);
                                     intent.putExtra("seniorKey", getRef(position).getKey());
                                     startActivity(intent);
                                 }
                             });
                        }

                        @NonNull
                        @Override
                        public SeniorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_senior, parent, false);
                            return new SeniorViewHolder(view);
                        }
                    };
                    adapter.startListening();
                    recyclerView.setAdapter(adapter);
                }
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
        // force app to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seniors, container, false);

        referenceSenior = FirebaseDatabase.getInstance().getReference("Users").child("Seniors");
        referenceAdmin = FirebaseDatabase.getInstance().getReference("Users").child("Admins");

        TextView tvDashboard = view.findViewById(R.id.tvDashboard);
        TextView tvTeam = view.findViewById(R.id.tvTeam);
        TextView tvCarer = view.findViewById(R.id.tvCarersTab);
        tvCountSeniors = view.findViewById(R.id.tvCountSeniors);

        tvDashboard.setOnClickListener(v -> replaceFragment.replaceFragment(new HomeFragment(), getActivity()));
        tvTeam.setOnClickListener(v -> replaceFragment.replaceFragment(new TeamFragment(), getActivity()));
        tvCarer.setOnClickListener(v -> replaceFragment.replaceFragment(new CarersFragment(), getActivity()));

        // search carer user
        androidx.appcompat.widget.SearchView searchview = view. findViewById(R.id.searchViewSeniors);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                // TODO BE ABLE TO SEARCH WITH IGNORECASE

                displaySeniors(newText);
                return false;
            }
        });

        displaySeniors("");

        return view;
    }
}