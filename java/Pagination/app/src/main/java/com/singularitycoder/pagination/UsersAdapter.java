package com.singularitycoder.pagination;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "NewsAdapter";

    ArrayList<UsersSubItemData> userList;
    Context context;

    UsersAdapter(ArrayList<UsersSubItemData> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UsersSubItemData usersSubItemData = userList.get(position);
        if (null != holder) {
            UserViewHolder userViewHolder = (UserViewHolder) holder;
            userViewHolder.tvName.setText(usersSubItemData.getFirstName() + " " + usersSubItemData.getLastName());
            userViewHolder.tvEmail.setText(usersSubItemData.getEmail());
            glideImage(context, usersSubItemData.getAvatar(), userViewHolder.ivUserImage);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private static void glideImage(Context context, String imgUrl, ImageView imageView) {
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.color.colorAccent)
                .error(R.mipmap.ic_launcher)
//                .transform(new CenterCrop(), new RoundedCorners(60))
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);

        Glide.with(context)
                .load(imgUrl)
                .apply(requestOptions)
                .into(imageView);
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvEmail;
        ImageView ivUserImage;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvEmail = itemView.findViewById(R.id.tv_email);
            ivUserImage = itemView.findViewById(R.id.iv_user_image);
        }
    }
}