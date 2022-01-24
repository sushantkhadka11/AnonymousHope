package com.sushant.anonymoushope.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushant.anonymoushope.Adapter.HomeAdapter;
import com.sushant.anonymoushope.Model.Posts;
import com.sushant.anonymoushope.Model.User;
import com.sushant.anonymoushope.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {



    CircleImageView profileImage;
    TextView name, noOfPosts, donated, received, bio, gender, phoneNumber, email;
    FirebaseUser firebaseUser;
    Button btnMessage;
    LinearLayout linearAbout, linearRecyclerView;
    ImageView btnAbout, btnPosts;
    List<Posts> posts;
    RecyclerView postRecyclerView;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        posts = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileImage= findViewById(R.id.proImage);
        name =findViewById(R.id.proName);
        noOfPosts =findViewById(R.id.proPost);
        donated =findViewById(R.id.proDonated);
        received =findViewById(R.id.proReceived);
        bio =findViewById(R.id.profileBio);
        gender =findViewById(R.id.profileGender);
        phoneNumber =findViewById(R.id.profilePhoneNumber);
        email =findViewById(R.id.profileEmail);
        btnMessage =findViewById(R.id.btnMessagePerson);
        linearAbout= findViewById(R.id.linearAbout);
        linearRecyclerView= findViewById(R.id.linearReyclerView);
        btnAbout= findViewById(R.id.btnInfo);
        btnPosts= findViewById(R.id.btnPosts);
        postRecyclerView= findViewById(R.id.profileRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        postRecyclerView.setLayoutManager(layoutManager);
        btnPosts.setBackgroundColor(getResources().getColor(R.color.clickColor));
        linearAbout.setVisibility(View.GONE);
        linearRecyclerView.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        userId = intent.getStringExtra("UID");
        readProfile(userId);
        readPosts(userId);
        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearAbout.setVisibility(View.VISIBLE);
                btnPosts.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                btnAbout.setBackgroundColor(getResources().getColor(R.color.clickColor));
                linearRecyclerView.setVisibility(View.GONE);
            }
        });
        btnPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearRecyclerView.setVisibility(View.VISIBLE);
                btnPosts.setBackgroundColor(getResources().getColor(R.color.clickColor));
                btnAbout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                linearAbout.setVisibility(View.GONE);
            }
        });
        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });
    }

    public void readProfile(final String userId)
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getProfileImage().equals("Default"))
                    profileImage.setImageResource(R.drawable.download);
                else
                    Glide.with(getApplicationContext()).load(user.getProfileImage()).into(profileImage);
                name.setText(user.getFirstName()+" "+user.getLastName());
                bio.setText(user.getBIO());
                gender.setText(user.getGender());
                phoneNumber.setText(user.getPhoneNumber());
                email.setText(user.getEmail());
                donated.setText(""+user.getDonated());
                received.setText(""+user.getReceived());

                if(user.getUid().equals(firebaseUser.getUid()))
                {
                    btnMessage.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readPosts (final String userId)
    {
        posts.clear();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 :dataSnapshot.getChildren())
                {
                    Posts post = dataSnapshot1.getValue(Posts.class);
                    if(post.getUserId().equals(userId))
                    {
                        posts.add(post);
                    }

                }
                Collections.reverse(posts);
                noOfPosts.setText(posts.size()+"");
                HomeAdapter homeAdapter = new HomeAdapter(getApplicationContext(),posts,firebaseUser.getUid());
                postRecyclerView.setAdapter(homeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}