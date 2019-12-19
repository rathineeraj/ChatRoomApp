package com.chat.chatroom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder> {

    private GroupMessage groupMessage;
    private Context context;
    private List<GroupMessage> groupMessageList;


    public class ViewHolder extends RecyclerView.ViewHolder {

        public AppCompatTextView tvName, tvMessage;


        ViewHolder(View view) {
            super(view);

            tvName = view.findViewById(R.id.tvName);
            tvMessage = view.findViewById(R.id.tvMessage);

        }


    }

    public GroupMessageAdapter(Context mContext, List<GroupMessage> groupMessageList) {
        this.context = mContext;
        this.groupMessageList = groupMessageList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_message, parent, false);


        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        groupMessage = groupMessageList.get(position);
        if (groupMessage.getType() == 1) {
            holder.tvName.setText(groupMessage.getUserName());
            holder.tvMessage.setText(String.format(": %s", groupMessage.getMessage()));
        } else {
            holder.tvName.setText(String.format("%s %s", groupMessage.getUserName(), groupMessage.getMessage()));
            holder.tvMessage.setText("");

        }


    }

    @Override
    public int getItemCount() {
        return groupMessageList.size();
    }


}