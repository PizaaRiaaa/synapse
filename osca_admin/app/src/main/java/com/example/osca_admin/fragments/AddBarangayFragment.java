package com.example.osca_admin.fragments;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.osca_admin.R;
import com.example.osca_admin.util.PromptMessage;
import com.example.osca_admin.util.ReplaceFragment;
import com.example.osca_admin.util.readwrite.ReadWriteBarangay;
import com.example.osca_admin.util.viewholder.BarangayViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddBarangayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddBarangayFragment extends Fragment {

    private DatabaseReference referenceBarangay;
    TextInputEditText inputBarangay;
    PromptMessage promptMessage = new PromptMessage();
    TextView totalBarangay;
    RecyclerView recyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddBarangayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddBarangayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddBarangayFragment newInstance(String param1, String param2) {
        AddBarangayFragment fragment = new AddBarangayFragment();
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

    void displayBarangay() {
        referenceBarangay.child("barangay").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ignored : snapshot.getChildren()) {
                        Query query = snapshot.getRef().orderByChild("brgy");
                        FirebaseRecyclerOptions<ReadWriteBarangay> options = new FirebaseRecyclerOptions.Builder<ReadWriteBarangay>().setQuery(query, ReadWriteBarangay.class).build();
                        FirebaseRecyclerAdapter<ReadWriteBarangay, BarangayViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteBarangay, BarangayViewHolder>(options) {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @SuppressLint("SetTextI18n")
                            @Override
                            protected void onBindViewHolder(@NonNull BarangayViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteBarangay model) {

                                String name = model.getBrgy();
                                holder.barangayName.setText(name);

                                displayTotalBarangay();

                                // holder.itemView.setOnClickListener(new View.OnClickListener() {
                                //     @Override
                                //     public void onClick(View v) {
                                //         Intent intent = new Intent(getActivity(), ViewMedicine.class);
                                //         intent.putExtra("userKey", getRef(position).getKey());
                                //         startActivity(intent);
                                //     }
                                // });
                            }

                            @NonNull
                            @Override
                            public BarangayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_barangay, parent, false);
                                return new BarangayViewHolder(view);
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

    void addBarangay(){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("brgy", inputBarangay.getText().toString());
        referenceBarangay.child("barangay").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    promptMessage.displayMessage(
                            "Success",
                            "Barangay has been added successfully",
                            R.color.dark_green, getActivity());
                    inputBarangay.setText("");
                }else{
                    promptMessage.defaultErrorMessage(getActivity());
                }
            }
        });
    }

    void validateInputBarangay(){
        if(TextUtils.isEmpty(inputBarangay.getText())){
            promptMessage.displayMessage(
                    "Empty field",
                    "Please input barangay name",
                    R.color.red1, getActivity());
        }else{
            addBarangay();
        }
    }

    void displayTotalBarangay(){
        referenceBarangay.child("barangay").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count_barangay = (int) snapshot.getChildrenCount();
                String count = String.valueOf(count_barangay);
                totalBarangay.setText(count);
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
        recyclerView = view.findViewById(R.id.recyclerview_barangay);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // force app to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_barangay, container, false);

        referenceBarangay = FirebaseDatabase.getInstance().getReference("MandaluyongBarangay");

        ReplaceFragment replaceFragment = new ReplaceFragment();

        MaterialButton btnAddBarangay = view.findViewById(R.id.btnAdd);
        MaterialButton btnClearText = view.findViewById(R.id.btnClear);
        ImageView ibBack = view.findViewById(R.id.ibBack);
        totalBarangay = view.findViewById(R.id.tvTotalBarangay);
        inputBarangay = view.findViewById(R.id.etAddBarangay);

        btnAddBarangay.setOnClickListener(v -> validateInputBarangay());
        btnClearText.setOnClickListener(v -> inputBarangay.setText(""));
        ibBack.setOnClickListener(v -> replaceFragment.replaceFragment(new HomeFragment(), getActivity()));

        displayBarangay();

        return view;
    }
}