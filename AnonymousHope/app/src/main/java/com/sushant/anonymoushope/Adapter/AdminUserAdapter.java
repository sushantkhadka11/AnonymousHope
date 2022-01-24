package com.sushant.anonymoushope.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sushant.anonymoushope.Activity.MessageActivity;
import com.sushant.anonymoushope.Model.Notification;
import com.sushant.anonymoushope.Model.Posts;
import com.sushant.anonymoushope.Model.User;
import com.sushant.anonymoushope.R;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.ViewHolder> {
    private Context myContext;
    private List<User> users;

    public AdminUserAdapter(Context myContext, List<User> users) {
        this.myContext = myContext;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(myContext).inflate(R.layout.post_display,parent,false);

        return new AdminUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.content.setText(user.getBIO());
        holder.fullName.setText(user.getFirstName()+' '+ user.getLastName());
        if (isValidContextForGlide(myContext)) {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.cardView.getLayoutParams();

            holder.homeLinearLayout.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            if (user.getProfileImage().equals("Default"))
            {
                holder.userProfileImage.setImageResource(R.drawable.anonymous);
            }
            else
            {
                Glide.with(myContext).load(user.getProfileImage()).into(holder.userProfileImage);
            }
            if(user.getBioMetrics().equals("Default"))
            {
                holder.imageView.setImageResource(R.drawable.noimage);
            }
            else
            {
                Glide.with(myContext).load(user.getBioMetrics()).into(holder.imageView);

            }


            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("accepted", true);
                    reference.updateChildren(hashMap);



                }
            });
            holder.btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("AdminAccepted", true);
                    reference.updateChildren(hashMap);


                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView userProfileImage;

        ImageView imageView;
        TextView fullName, content, gender ;
        VideoView videoView;
        Button btnAccept, btnDecline;
        CardView cardView;
        LinearLayout homeLinearLayout;


        public ViewHolder(@NonNull View itemView) {
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
