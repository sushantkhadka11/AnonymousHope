package com.sushant.anonymoushope.Adapter;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sushant.anonymoushope.Model.Notification;
import com.sushant.anonymoushope.Model.Posts;
import com.sushant.anonymoushope.Model.User;
import com.sushant.anonymoushope.R;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminPostAdapter extends RecyclerView.Adapter<AdminPostAdapter.Viewholder> {

    private Context myContext;
    private List<Posts> events;

    public AdminPostAdapter(Context myContext, List<Posts> events) {
        this.myContext = myContext;
        this.events = events;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.post_display,parent,false);
        return new AdminPostAdapter.Viewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {
        final Posts event = events.get(position);
        holder.content.setText(event.getContent());
        if (isValidContextForGlide(myContext)) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.cardView.getLayoutParams();
            if (event.getType().equals("video")) {
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


            } else if (event.getType().equals("image")) {
                holder.imageView.setVisibility(View.VISIBLE);
                holder.homeLinearLayout.setVisibility(View.GONE);
                Glide.with(myContext).load(event.getMedia()).into(holder.imageView);
                lp.addRule(RelativeLayout.BELOW, holder.imageView.getId());

            } else {
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

            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(event.getPostId());
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("accepted",true);
                    reference.updateChildren(hashMap);

                    Notification notification = new Notification(true,event.getPostId(),"Accepted",event.getUserId(),false);
                    FirebaseDatabase.getInstance().getReference("Notifications").child(event.getUserId()).setValue(notification);

                }
            });
            holder.btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query eventQuery = ref.child("Posts").child(event.getPostId());
                    eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot dataSnapshot: snapshot.getChildren())
                            {
                                dataSnapshot.getRef().removeValue();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Notification notification = new Notification(true,event.getPostId(),"Declined",event.getUserId(),false);
                    FirebaseDatabase.getInstance().getReference("Notifications").child(event.getUserId()).setValue(notification);

                }
            });
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(event.getUserId());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    holder.fullName.setText(user.getFirstName()+ " "+user.getLastName());
                    holder.gender.setText(user.getGender());
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount () {
        return events.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        CircleImageView userProfileImage;

        ImageView imageView;
        TextView fullName, content, gender ;
        VideoView videoView;
        Button btnAccept, btnDecline;
        CardView cardView;
        LinearLayout homeLinearLayout;


        public Viewholder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.userProfileImage);
            imageView = itemView.findViewById(R.id.AdminContentImage);
            fullName = itemView.findViewById(R.id.proUsername);
            gender = itemView.findViewById(R.id.gender);
            content = itemView.findViewById(R.id.eventContent);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDecline = itemView.findViewById(R.id.btnDecline);
            videoView = itemView.findViewById(R.id.ContentVideoView);
            cardView = itemView.findViewById(R.id.adminCardView);
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
