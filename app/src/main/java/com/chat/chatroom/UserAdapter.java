package com.chat.chatroom;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private User user;
    private Context context;
    private List<User> userList;

    private final OnItemClickListener listener;


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
                    user=userList.get(getAdapterPosition());
                    listener.onItemClick(user);
                }
            });

           /* tvUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {





                }
            });*/

        }


    }

    public UserAdapter(Context mContext, List<User> userList,OnItemClickListener listener) {
        this.context = mContext;
        this.userList = userList;
        this.listener = listener;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        user = userList.get(position);
        if (TextUtils.equals(user.getGender(), "Male")) {
            holder.ivUser.setImageResource(R.drawable.boy_icon);
        } else {
            holder.ivUser.setImageResource(R.drawable.girl_icon);


        }

        holder.tvUser.setText(user.getName());


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


}