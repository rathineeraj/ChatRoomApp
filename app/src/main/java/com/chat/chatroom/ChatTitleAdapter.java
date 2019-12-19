package com.chat.chatroom;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ChatTitleAdapter extends RecyclerView.Adapter<ChatTitleAdapter.ViewHolder> {

    private ChatTitle chatTitle;
    private Context context;
    private List<ChatTitle> chatTitleList;
    private String mUserId;

    private final OnItemClickListenerChatTitle listener;


    public class ViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView tvUser;
        AppCompatImageView ivUser;


        ViewHolder(View view) {
            super(view);

            tvUser = view.findViewById(R.id.tvUser);
            ivUser = view.findViewById(R.id.ivUser);

            tvUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chatTitle = chatTitleList.get(getAdapterPosition());
                     listener.onItemClick(chatTitle);
                }
            });

           /* tvUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {





                }
            });*/

        }


    }

    public ChatTitleAdapter(Context mContext, List<ChatTitle> chatTitleList, String mUserId, OnItemClickListenerChatTitle listener) {
        this.context = mContext;
        this.chatTitleList = chatTitleList;
        this.mUserId = mUserId;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_title, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        chatTitle = chatTitleList.get(position);


        if (chatTitle.getFromUserId().equals(mUserId)) {
            holder.tvUser.setText(chatTitle.getToUser());
        } else {
            holder.tvUser.setText(chatTitle.getFromUser());


        }

       // //


    }

    @Override
    public int getItemCount() {
        return chatTitleList.size();
    }


}