package com.sushant.anonymoushope.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushant.anonymoushope.Activity.LoginActivity;
import com.sushant.anonymoushope.Activity.SettingsActivity;
import com.sushant.anonymoushope.Adapter.HomeAdapter;
import com.sushant.anonymoushope.Model.Posts;
import com.sushant.anonymoushope.Model.User;
import com.sushant.anonymoushope.R;
import com.sushant.anonymoushope.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsFragment extends Fragment {


    CircleImageView profileImage;
    TextView name, noOfPosts, donated, received, bio, gender, phoneNumber, email;
    FirebaseUser firebaseUser;
    Button btnSettings;
    List<Posts> posts;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root=  inflater.inflate(R.layout.fragment_notifications, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileImage= root.findViewById(R.id.proImage);
        name =root.findViewById(R.id.proName);
        noOfPosts =root.findViewById(R.id.proPost);
        donated =root.findViewById(R.id.proDonated);
        received =root.findViewById(R.id.proReceived);
        bio =root.findViewById(R.id.profileBio);
        gender =root.findViewById(R.id.profileGender);
        posts = new ArrayList<>();
        phoneNumber =root.findViewById(R.id.profilePhoneNumber);
        email =root.findViewById(R.id.profileEmail);
        btnSettings =root.findViewById(R.id.btnSettings);
        readProfile(firebaseUser.getUid());
        readPosts(firebaseUser.getUid());
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        return root;
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
                    Glide.with(getActivity()).load(user.getProfileImage()).into(profileImage);
                name.setText(user.getFirstName()+" "+user.getLastName());
                bio.setText(user.getBIO());
                gender.setText(user.getGender());
                phoneNumber.setText(user.getPhoneNumber());
                email.setText(user.getEmail());
                donated.setText(user.getDonated());
                received.setText(user.getReceived());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readPosts (final String userId) {
        posts.clear();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Posts post = dataSnapshot1.getValue(Posts.class);
                    if (post.getUserId().equals(userId)) {
                        posts.add(post);
                    }

                }
                Collections.reverse(posts);
                noOfPosts.setText(posts.size() + "");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}