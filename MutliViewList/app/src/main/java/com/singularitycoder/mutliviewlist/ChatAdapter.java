package com.singularitycoder.mutliviewlist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ChatAdapter";

    public static final int VIEW_TYPE_SENDER_TEXT_MESSAGE = 0;
    public static final int VIEW_TYPE_SENDER_IMAGE_MESSAGE = 1;
    public static final int VIEW_TYPE_RECEIVER_TEXT_MESSAGE = 2;
    public static final int VIEW_TYPE_RECEIVER_TEXT_BROKEN_MESSAGE = 3;

    private Context context;
    private ArrayList<ChatItem> chatList;

    public ChatAdapter(Context context, ArrayList<ChatItem> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENDER_TEXT_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_text_sender, parent, false);
            return new TextSenderViewHolder(view);
        } else if (viewType == VIEW_TYPE_SENDER_IMAGE_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_image_sender, parent, false);
            return new ImageSenderViewHolder(view);
        } else if (viewType == VIEW_TYPE_RECEIVER_TEXT_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_text_receiver, parent, false);
            return new TextReceiverViewHolder(view);
        } else if (viewType == VIEW_TYPE_RECEIVER_TEXT_BROKEN_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_text_receiver_broken, parent, false);
            return new TextReceiverBrokenViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatItem chatItem = chatList.get(position);
        if (null != holder) {
            if (holder instanceof TextSenderViewHolder) {
                TextSenderViewHolder textSenderViewHolder = ((TextSenderViewHolder) holder);
                textSenderViewHolder.tvSenderName.setText(chatItem.getStrName());
                textSenderViewHolder.tvSenderTextMessage.setText(chatItem.getStrTextMessage());
                textSenderViewHolder.tvSenderDate.setText(chatItem.getStrDate());
                textSenderViewHolder.ivSenderProfileImage.setImageResource(chatItem.getIntProfileImage());
            }

            if (holder instanceof ImageSenderViewHolder) {
                ImageSenderViewHolder imageSenderViewHolder = ((ImageSenderViewHolder) holder);
                imageSenderViewHolder.tvSenderName.setText(chatItem.getStrName());
                imageSenderViewHolder.ivSenderImageMessage.setImageResource(chatItem.getIntImageMessage());
                imageSenderViewHolder.tvSenderDate.setText(chatItem.getStrDate());
                imageSenderViewHolder.ivSenderProfileImage.setImageResource(chatItem.getIntProfileImage());
            }

            if (holder instanceof TextReceiverViewHolder) {
                TextReceiverViewHolder textReceiverViewHolder = ((TextReceiverViewHolder) holder);
                textReceiverViewHolder.tvReceiverName.setText(chatItem.getStrName());
                textReceiverViewHolder.tvReceiverTextMessage.setText(chatItem.getStrTextMessage());
                textReceiverViewHolder.tvReceiverDate.setText(chatItem.getStrDate());
                textReceiverViewHolder.ivReceiverProfileImage.setImageResource(chatItem.getIntProfileImage());
            }

            if (holder instanceof TextReceiverBrokenViewHolder) {
                TextReceiverBrokenViewHolder textReceiverBrokenViewHolder = ((TextReceiverBrokenViewHolder) holder);
                textReceiverBrokenViewHolder.tvReceiverName.setText(chatItem.getStrName());
                textReceiverBrokenViewHolder.tvReceiverBrokenTextMessage.setText(chatItem.getStrTextMessage());
                textReceiverBrokenViewHolder.tvReceiverDate.setText(chatItem.getStrDate());
                textReceiverBrokenViewHolder.ivReceiverProfileImage.setImageResource(chatItem.getIntProfileImage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatList.get(position).getStrUserType().equals("sender")) {
            if (chatList.get(position).getStrMessageType().equals("text")) {
                // Text Message
                return VIEW_TYPE_SENDER_TEXT_MESSAGE;
            } else {
                // Image Message
                return VIEW_TYPE_SENDER_IMAGE_MESSAGE;
            }
        } else {
            if (chatList.get(position).getStrMessageType().equals("text")) {
                // Text Message
                return VIEW_TYPE_RECEIVER_TEXT_MESSAGE;
            } else {
                // Text Broken Message
                return VIEW_TYPE_RECEIVER_TEXT_BROKEN_MESSAGE;
            }
        }
    }

    class TextSenderViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName;
        ImageView ivSenderProfileImage;
        TextView tvSenderDate;
        TextView tvSenderTextMessage;

        public TextSenderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tv_sender_name);
            ivSenderProfileImage = itemView.findViewById(R.id.iv_sender_profile_pic);
            tvSenderDate = itemView.findViewById(R.id.tv_sender_date_posted);
            tvSenderTextMessage = itemView.findViewById(R.id.tv_sender_message);
        }
    }

    class ImageSenderViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName;
        ImageView ivSenderProfileImage;
        TextView tvSenderDate;
        ImageView ivSenderImageMessage;

        public ImageSenderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tv_sender_name);
            ivSenderProfileImage = itemView.findViewById(R.id.iv_sender_profile_pic);
            tvSenderDate = itemView.findViewById(R.id.tv_sender_date_posted);
            ivSenderImageMessage = itemView.findViewById(R.id.iv_sender_image_message);
        }
    }

    class TextReceiverViewHolder extends RecyclerView.ViewHolder {
        TextView tvReceiverName;
        ImageView ivReceiverProfileImage;
        TextView tvReceiverDate;
        TextView tvReceiverTextMessage;

        public TextReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiverName = itemView.findViewById(R.id.tv_receiver_name);
            ivReceiverProfileImage = itemView.findViewById(R.id.iv_receiver_profile_pic);
            tvReceiverDate = itemView.findViewById(R.id.tv_receiver_date_posted);
            tvReceiverTextMessage = itemView.findViewById(R.id.tv_receiver_message);
        }
    }

    class TextReceiverBrokenViewHolder extends RecyclerView.ViewHolder {
        TextView tvReceiverName;
        ImageView ivReceiverProfileImage;
        TextView tvReceiverDate;
        TextView tvReceiverBrokenTextMessage;

        public TextReceiverBrokenViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiverName = itemView.findViewById(R.id.tv_receiver_name);
            ivReceiverProfileImage = itemView.findViewById(R.id.iv_receiver_profile_pic);
            tvReceiverDate = itemView.findViewById(R.id.tv_receiver_date_posted);
            tvReceiverBrokenTextMessage = itemView.findViewById(R.id.tv_receiver_message);
        }
    }
}
