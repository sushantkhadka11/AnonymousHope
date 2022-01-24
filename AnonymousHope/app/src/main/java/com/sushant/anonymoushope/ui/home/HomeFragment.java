package com.sushant.anonymoushope.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushant.anonymoushope.Activity.MessageActivity;
import com.sushant.anonymoushope.Activity.MessageListActivity;
import com.sushant.anonymoushope.Activity.NotificationActivity;
import com.sushant.anonymoushope.Adapter.HomeAdapter;
import com.sushant.anonymoushope.Model.Posts;
import com.sushant.anonymoushope.R;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {


    RecyclerView recyclerView;
    ImageView btnMessage, btnNotification;
    List<Posts> events;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    HomeAdapter homeAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root=  inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.homeRecyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        events = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        btnMessage= root.findViewById(R.id.img_message);
        btnNotification= root.findViewById(R.id.img_notification);

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessageListActivity.class);
                startActivity(intent);
            }
        });
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NotificationActivity.class);
                startActivity(intent);
            }
        });
        readEvents();
        return root;
    }
    public void readEvents()
    {

        reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    Posts event = dataSnapshot1.getValue(Posts.class);
                    if(event.getAccepted()== true)
                    {
                        events.add(event);

                    }

                }
                Collections.reverse(events);
                homeAdapter = new HomeAdapter(getContext(),events,firebaseUser.getUid());
                recyclerView.setAdapter(homeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

}