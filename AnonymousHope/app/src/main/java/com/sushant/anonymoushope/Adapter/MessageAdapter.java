package com.sushant.anonymoushope.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sushant.anonymoushope.Model.Chat;
import com.sushant.anonymoushope.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static  final  int MSG_TYPE_LEFT=0;
    public static  final  int MSG_TYPE_RIGHT=1;
    private Context context;
    private List<Chat> chats;
    private String imgUrl;

    FirebaseUser firebaseUser;
    public MessageAdapter(Context context, List<Chat> chats, String imgUrl ){
        this.context = context;
        this.chats = chats;
        this.imgUrl=imgUrl;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new ViewHolder(view);
        }    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.showMessage.setText(chat.getMessage());
        if(imgUrl.equals("Default"))
        {
            holder.profile.setImageResource(R.drawable.download);
        }
        else {
            Glide.with(context.getApplicationContext()).load(imgUrl).into(holder.profile);
        }

    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(chats.get(position).getMsgSender().equals((firebaseUser.getUid())))
        {
            return MSG_TYPE_RIGHT;
        }
        else
        {
            return MSG_TYPE_LEFT;
        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        public TextView showMessage;
        public CircleImageView profile;
        private ImageView mgsImg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            showMessage=itemView.findViewById(R.id.show_message);
            profile=itemView.findViewById(R.id.profile_image);

        }
    }
}
