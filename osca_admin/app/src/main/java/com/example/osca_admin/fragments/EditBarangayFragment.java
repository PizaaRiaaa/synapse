package com.example.osca_admin.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osca_admin.R;
import com.example.osca_admin.util.PromptMessage;
import com.example.osca_admin.util.ReplaceFragment;
import com.example.osca_admin.util.readwrite.ReadWriteBarangay;
import com.example.osca_admin.util.viewholder.BarangayViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditBarangayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditBarangayFragment extends Fragment {

    DatabaseReference referenceBarangay;
    PromptMessage promptMessage;
    ReplaceFragment replaceFragment;
    RecyclerView recyclerView;

    TextView totalBarangay;
    ImageView ivBack;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EditBarangayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditBarangayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditBarangayFragment newInstance(String param1, String param2) {
        EditBarangayFragment fragment = new EditBarangayFragment();
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
        recyclerView = view.findViewById(R.id.recyclerview_edit_barangay);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
    }

    void editBarangay() {
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
                            holder.editBarangayName.setText(name);

                            holder.editButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(model.getBrgy())
                                            .setPositiveButton("Update",(dialogInterface, i) ->
                                                    updateBarangay(getRef(position).getKey(),
                                                            holder.editBarangayName.getText().toString()))
                                            .setNegativeButton("Delete",(dialogInterface, i) ->
                                                    displayDialogForDelete(getRef(position).getKey(),
                                                            model.getBrgy()))
                                            .setNeutralButton("Cancel", (dialogInterface, i) -> dialogInterface.cancel())
                                            .setCancelable(false)
                                            .show();
                                }
                            });

                            displayTotalBarangay();
                        }

                        @NonNull
                        @Override
                        public BarangayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_edit_barangay, parent, false);
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


    void deleteBarangay(String brgyKey){
        referenceBarangay.child("barangay").child(brgyKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   referenceBarangay.child("barangay").child(brgyKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                              promptMessage.displayMessage(
                                      "Sucessful",
                                      "Barangay has been deleted successfully",
                                      R.color.dark_green, getActivity());
                           }
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

    void displayDialogForDelete(String brgyKey, String barangayName){
        new AlertDialog.Builder(getActivity())
                .setTitle(barangayName)
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("YES",(dialogInterface, i) -> deleteBarangay(brgyKey))
                .setNegativeButton("CANCEL",(dialogInterface, i) -> dialogInterface.cancel())
                .setCancelable(false)
                .show();
    }

    void updateBarangay(String brgyKey, String newName){
        referenceBarangay.child("barangay").child(brgyKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot.exists()){
                   HashMap<String, Object> hashMap = new HashMap<String, Object>();
                   hashMap.put("brgy",newName);
                   referenceBarangay.child("barangay").child(brgyKey).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               promptMessage.displayMessage(
                                       "Success",
                                       "item has been updated successfully",
                                       R.color.dark_green, getActivity());
                           }
                       }
                   });
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // force app to light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_barangay, container, false);

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        replaceFragment = new ReplaceFragment();
        promptMessage = new PromptMessage();

        referenceBarangay = FirebaseDatabase.getInstance().getReference("MandaluyongBarangay");

        totalBarangay = view.findViewById(R.id.tvTotalBarangay);
        ivBack = view.findViewById(R.id.ibBack);

        ivBack.setOnClickListener(v -> replaceFragment.replaceFragment(new HomeFragment(), getActivity()));


        editBarangay();

        return view;
    }
}