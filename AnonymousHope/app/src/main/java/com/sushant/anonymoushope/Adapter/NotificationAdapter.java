package com.sushant.anonymoushope.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.sushant.anonymoushope.Model.Notification;
import com.sushant.anonymoushope.R;

import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private Context context;
    private List<Notification> notifications;

    public NotificationAdapter(Context context, List<Notification> notifications){
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_display,parent,false);
        return new ViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        if (notification.getAdmin().equals(true)){
            holder.username.setText("Admin");
            holder.profile.setImageResource(R.drawable.admin);
            holder.description.setText(" Your post "+notification.getPostId()+" has been "+ notification.getText()+ " for donation on Anonymous Hope");
        }
        else if(notification.getDonation().equals(true)){
            holder.description.setText("An anonymous person has donated $"+notification.getAmount()+"\n Post Id: "+notification.getPostId()+"\n Remarks: "+notification.getText());
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username,description;
        public ImageView profile;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.notification_fullName);
            description=itemView.findViewById(R.id.notification_content);
            profile=itemView.findViewById(R.id.notification_profile_image);


        }
    }

}
