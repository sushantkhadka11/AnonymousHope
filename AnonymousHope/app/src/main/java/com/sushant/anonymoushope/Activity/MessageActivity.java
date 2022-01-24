package com.sushant.anonymoushope.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.sushant.anonymoushope.Adapter.MessageAdapter;
import com.sushant.anonymoushope.MainActivity;
import com.sushant.anonymoushope.Model.Chat;
import com.sushant.anonymoushope.Model.User;
import com.sushant.anonymoushope.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {


    CircleImageView profile_image;
    TextView tvusername;
    List<Chat> chats;
    FirebaseUser firebaseUser;
    DatabaseReference refrences;
    private String userid;
    MessageAdapter messageAdapter;
    Intent intent;
    RecyclerView recyclerView;
    ImageButton btnSendMessage;
    EditText texmessage;
    boolean notify = false;
    ProgressBar progressBar;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        profile_image= findViewById(R.id.profile_image);
        tvusername=findViewById(R.id.username);
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        btnSendMessage=findViewById(R.id.btnsend);
        texmessage=findViewById(R.id.sendmessage);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.messageBtnBack);
        intent=getIntent();
        userid = intent.getStringExtra("userId");
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify=true;
                String message= texmessage.getText().toString();
                if(!message.equals(""))
                {
                    sendMessage(firebaseUser.getUid(),userid,message);
                    texmessage.setText("");
                }
                else
                {
                    Toast.makeText(MessageActivity.this, "There was error sending the message", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        refrences= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        refrences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user= dataSnapshot.getValue(User.class);
                tvusername.setText(user.getFirstName()+" "+user.getLastName());
                if(user.getProfileImage().equals("Default"))
                {
                    profile_image.setImageResource(R.drawable.anonymous);
                }
                else {
                    Glide.with(getApplicationContext()).load(user.getProfileImage()).into(profile_image);
                }

                readMessage(firebaseUser.getUid(),userid,user.getProfileImage());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void sendMessage(final String sender, final String reciver, String message)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("msgSender",sender);
        hashMap.put("msgReceiver",reciver);
        hashMap.put("message",message);
        reference.child("Chat").push().setValue(hashMap);

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userid);
        chatRefReceiver.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    chatRefReceiver.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        final DatabaseReference chatRefReceiver2 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userid)
                .child(firebaseUser.getUid());
        chatRefReceiver2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists())
                {
                    chatRefReceiver2.child("id").setValue(firebaseUser.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;
        DatabaseReference notific = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        notific.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(notify)
                {
                }

                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    private void readMessage(final String myid, final String userid, final String imgUrl)
    {
        chats = new ArrayList<>();
        refrences = FirebaseDatabase.getInstance().getReference("Chat");
        refrences.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getMsgReceiver().equals(userid) && chat.getMsgSender().equals(myid)||
                            chat.getMsgReceiver().equals(myid) && chat.getMsgSender().equals(userid))
                    {
                        chats.add(chat);
                    }


                }
                messageAdapter = new MessageAdapter(getApplicationContext(),chats,imgUrl);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}