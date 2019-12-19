package com.chat.chatroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Chat chat;
    private Context context;
    private String userId;
    private List<Chat> chatList;


    public class ViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView tvNameLeft, tvMessageLeft, tvDateLeft, tvNameRight, tvMessageRight, tvDateRight;
        CardView linearLeft, linearRight;
        AppCompatImageView ivLeft, ivRight;


        ViewHolder(View view) {
            super(view);

            linearLeft = view.findViewById(R.id.linearLeft);
            linearRight = view.findViewById(R.id.linearRight);
            tvNameLeft = view.findViewById(R.id.tvNameLeft);
            tvMessageLeft = view.findViewById(R.id.tvMessageLeft);
            tvDateLeft = view.findViewById(R.id.tvDateLeft);
            tvNameRight = view.findViewById(R.id.tvNameRight);
            tvMessageRight = view.findViewById(R.id.tvMessageRight);
            tvDateRight = view.findViewById(R.id.tvDateRight);
            ivLeft = view.findViewById(R.id.ivLeft);
            ivRight = view.findViewById(R.id.ivRight);

        }


    }

    public ChatAdapter(Context mContext, List<Chat> chatList, String userId) {
        this.context = mContext;
        this.chatList = chatList;
        this.userId = userId;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        chat = chatList.get(position);


        if (chat.getFromUserId().equals(userId)) {
            holder.linearRight.setVisibility(View.VISIBLE);
            holder.linearLeft.setVisibility(View.GONE);
            holder.tvNameRight.setText(chat.getFromName());
            holder.tvMessageRight.setText(chat.getMessage());
            holder.tvDateRight.setText(chat.getMessageDate());

            if (chat.getType() == 0) {

                holder.tvMessageRight.setVisibility(View.VISIBLE);
                holder.ivRight.setVisibility(View.GONE);
            } else {
                holder.tvMessageRight.setVisibility(View.GONE);
                holder.ivRight.setVisibility(View.VISIBLE);
                Picasso.get().load(chat.getMessage()).into(holder.ivRight);

            }


        } else {

            holder.linearRight.setVisibility(View.GONE);
            holder.linearLeft.setVisibility(View.VISIBLE);
            holder.tvNameLeft.setText(chat.getFromName());
            holder.tvMessageLeft.setText(chat.getMessage());
            holder.tvDateLeft.setText(chat.getMessageDate());

            if (chat.getType() == 0) {

                holder.tvMessageLeft.setVisibility(View.VISIBLE);
                holder.ivLeft.setVisibility(View.GONE);
            } else {
                holder.tvMessageLeft.setVisibility(View.GONE);
                holder.ivLeft.setVisibility(View.VISIBLE);
                Picasso.get().load(chat.getMessage()).into(holder.ivLeft);

            }

        }


    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


}