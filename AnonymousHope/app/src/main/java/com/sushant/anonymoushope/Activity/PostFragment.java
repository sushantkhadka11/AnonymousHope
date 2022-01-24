package com.sushant.anonymoushope.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushant.anonymoushope.Adapter.AdminPostAdapter;
import com.sushant.anonymoushope.Adapter.HomeAdapter;
import com.sushant.anonymoushope.Model.Posts;
import com.sushant.anonymoushope.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PostFragment extends Fragment {


    List<Posts> posts;
    private RecyclerView.LayoutManager layoutManager;
    AdminPostAdapter adminPostAdapter;
    RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_post, container, false);
        posts = new ArrayList<>();
        recyclerView = view.findViewById(R.id.adminPostRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    Posts event = dataSnapshot1.getValue(Posts.class);
                    Boolean accepted = event.getAccepted();
                    if(accepted==false)
                    {
                        posts.add(event);
                    }

                }
                Collections.reverse(posts);
                adminPostAdapter = new AdminPostAdapter(getContext(),posts);
                recyclerView.setAdapter(adminPostAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        return view;
    }
}