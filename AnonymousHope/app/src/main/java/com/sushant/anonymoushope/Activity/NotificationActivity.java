package com.sushant.anonymoushope.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushant.anonymoushope.Adapter.NotificationAdapter;
import com.sushant.anonymoushope.Model.Notification;
import com.sushant.anonymoushope.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    List<Notification> notifications;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        recyclerView = findViewById(R.id.recycler_notification);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        showNotification();
    }

    private void showNotification() {
        notifications = new ArrayList<>();
        notifications.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Notification notification = snapshot.getValue(Notification.class);
                    if(notification.getUserId().equals(firebaseUser.getUid()))
                    {
                        notifications.add(notification);
                    }
                }
                Collections.reverse(notifications);
                NotificationAdapter notificationAdapter = new NotificationAdapter(getApplicationContext(),notifications);
                recyclerView.setAdapter(notificationAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}