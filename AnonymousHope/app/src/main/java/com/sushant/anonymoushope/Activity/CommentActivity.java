package com.sushant.anonymoushope.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushant.anonymoushope.Adapter.CommentAdapter;
import com.sushant.anonymoushope.Model.Comment;
import com.sushant.anonymoushope.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {


    TextView noComments;
    EditText commentBox;
    ImageView sendComment;
    RecyclerView commentRecyclerView;
    FirebaseUser firebaseUser;
    List<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        final String postId = getIntent().getStringExtra("PostId");
        comments = new ArrayList<>();
        noComments= findViewById(R.id.NoComments);
        commentBox= findViewById(R.id.commentBox);
        sendComment= findViewById(R.id.sendComment);
        commentRecyclerView= findViewById(R.id.commentRecyclerView);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        readComments(postId);
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comments.removeAll(comments);
                sendComments(postId);
            }
        });

    }

    private void readComments(String PostId)
    {

        commentRecyclerView.setVisibility(View.VISIBLE);
        noComments.setVisibility(View.GONE);
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Activities").child(PostId).child("Comment");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.removeAll(comments);
                for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                {
                    Comment comment = dataSnapshot1.getValue(Comment.class);
                    comments.add(comment);
                }
                Collections.reverse(comments);
                if (comments.size()==0)
                {
                    commentRecyclerView.setVisibility(View.GONE);
                    noComments.setVisibility(View.VISIBLE);

                }
                else
                {
                    CommentAdapter commentAdapter = new CommentAdapter(comments,getApplicationContext());
                    commentRecyclerView.setAdapter(commentAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendComments(String postId)
    {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Activities").child(postId).child("Comment");
        String commentId = reference.push().getKey();
        String comment = commentBox.getText().toString();
        if (comment.equals(""))
        {
            Toast.makeText(this, "Comment box is empty", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap hashMap = new HashMap();
            hashMap.put("id",commentId);
            hashMap.put("comment",comment);
            hashMap.put("userId",firebaseUser.getUid());
            reference.push().setValue(hashMap);
            comments.removeAll(comments);
            Toast.makeText(this, "Comment send", Toast.LENGTH_SHORT).show();
            commentBox.setText("");
            readComments(postId);
        }

    }
}