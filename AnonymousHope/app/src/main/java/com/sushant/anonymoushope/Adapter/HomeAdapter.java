package com.sushant.anonymoushope.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sushant.anonymoushope.Activity.CommentActivity;
import com.sushant.anonymoushope.Activity.DonateActivity;
import com.sushant.anonymoushope.Activity.ProfileActivity;
import com.sushant.anonymoushope.Model.Posts;
import com.sushant.anonymoushope.Model.User;
import com.sushant.anonymoushope.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.Viewholder> {
    private Context myContext;
    private List<Posts> events;
    private String uid;
    FirebaseUser firebaseUser;

    public HomeAdapter(Context myContext, List<Posts> events, String uid) {
        this.myContext = myContext;
        this.events = events;
        this.uid = uid;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.home_display,parent,false);
        return new Viewholder(view);    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Posts event = events.get(position);
        holder.content.setText(event.getContent());
        if (isValidContextForGlide(myContext)){
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.cardView.getLayoutParams();
            if(event.getType().equals("video"))
            {
                holder.imageView.setVisibility(View.GONE);
                holder.homeLinearLayout.setVisibility(View.VISIBLE);
                holder.videoView.setVideoURI(Uri.parse(event.getMedia()));
                holder.videoView.start();
                holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
                lp.addRule(RelativeLayout.BELOW, holder.homeLinearLayout.getId());


            }
            else if (event.getType().equals("image")){
                holder.imageView.setVisibility(View.VISIBLE);
                holder.homeLinearLayout.setVisibility(View.GONE);
                Glide.with(myContext).load(event.getMedia()).into(holder.imageView);
                lp.addRule(RelativeLayout.BELOW, holder.imageView.getId());

            }
            else {
                holder.imageView.setVisibility(View.GONE);
                holder.homeLinearLayout.setVisibility(View.GONE);
                lp.addRule(RelativeLayout.BELOW, holder.content.getId());


            }
            holder.videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.videoView.setVideoURI(Uri.parse(event.getMedia()));
                    holder.videoView.start();
                    holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setLooping(true);
                        }
                    });
                }
            });
            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(myContext, CommentActivity.class);
                    intent.putExtra("PostId",event.getPostId());
                    myContext.startActivity(intent);
                }
            });
            holder.donate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(myContext, DonateActivity.class);
                    intent.putExtra("PostId",event.getPostId());
                    intent.putExtra("UserId",event.getUserId());
                    myContext.startActivity(intent);
                }
            });
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(event.getUserId());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    holder.gender.setText(user.getGender());
                    holder.fullName.setText(user.getFirstName()+" "+ user.getLastName());

                    if(user.getProfileImage().equals("Default"))
                    {
                        holder.userProfileImage.setImageResource(R.drawable.download);

                    }
                    else
                    {
                        Glide.with(myContext.getApplicationContext()).load(user.getProfileImage()).into(holder.userProfileImage);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        holder.userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(myContext, ProfileActivity.class);
                intent.putExtra("UID",event.getUserId());
                myContext.startActivity(intent);


            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }


    public static class Viewholder extends RecyclerView.ViewHolder{

        CircleImageView userProfileImage;

        ImageView imageView;
        TextView fullName, content, gender ;
        ImageView donate , comment;
        VideoView videoView;
        CardView cardView;
        LinearLayout homeLinearLayout;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            imageView = itemView.findViewById(R.id.ContentImage);
            fullName = itemView.findViewById(R.id.proUsername);
            gender = itemView.findViewById(R.id.gender);
            content = itemView.findViewById(R.id.eventContent);
            donate = itemView.findViewById(R.id.btnDonate);
            comment = itemView.findViewById(R.id.btnComment);
            videoView = itemView.findViewById(R.id.ContentVideoView);
            cardView = itemView.findViewById(R.id.homeCardView);
            homeLinearLayout = itemView.findViewById(R.id.homeLinearLayout);
        }
    }
    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

}
