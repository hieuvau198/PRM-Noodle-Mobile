package com.example.prm_noodle_mobile.customer.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.prm_noodle_mobile.R;
import java.util.List;

public class ChatbotAdapter extends RecyclerView.Adapter<ChatbotAdapter.ChatViewHolder> {
    private List<ChatMessageLocal> messages;

    public ChatbotAdapter(List<ChatMessageLocal> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatbot_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessageLocal msg = messages.get(position);
        holder.tvMessage.setText(msg.getMessage());
        if (msg.isBot()) {
            holder.imgAvatar.setImageResource(R.drawable.ic_robot); // cần thêm icon robot
            holder.tvMessage.setBackgroundResource(R.drawable.bg_bot_message);
            holder.tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.ic_user); // cần thêm icon user
            holder.tvMessage.setBackgroundResource(R.drawable.bg_user_message);
            holder.tvMessage.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(ChatMessageLocal msg) {
        messages.add(msg);
        notifyItemInserted(messages.size() - 1);
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvMessage;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_avatar);
            tvMessage = itemView.findViewById(R.id.tv_message);
        }
    }
} 