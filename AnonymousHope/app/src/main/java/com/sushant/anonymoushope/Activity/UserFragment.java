package com.sushant.anonymoushope.Activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.proto.ProtoOutputStream;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushant.anonymoushope.Adapter.AdminPostAdapter;
import com.sushant.anonymoushope.Adapter.AdminUserAdapter;
import com.sushant.anonymoushope.Model.Posts;
import com.sushant.anonymoushope.Model.User;
import com.sushant.anonymoushope.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class UserFragment extends Fragment {


    private RecyclerView.LayoutManager layoutManager;
    List<User> users;
    AdminUserAdapter adminUserAdapter;
    RecyclerView recyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_user, container, false);
        users = new ArrayList<>();

        recyclerView = view.findViewById(R.id.adminUserRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                {
                    User user = dataSnapshot1.getValue(User.class);
                    Boolean accepted = user.getAccepted();
                    if(accepted== false)
                    {
                        Boolean Adminaccepted = user.getAdminAccepted();
                        if(Adminaccepted==false)
                        {
                            users.add(user);

                        }
                    }

                }
                Collections.reverse(users);
                adminUserAdapter = new AdminUserAdapter(getContext(),users);
                recyclerView.setAdapter(adminUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        return view;
    }
}