package com.example.pfg_frontend;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isFromUser() ? 1 : 2;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = (viewType == 1)
                ? inflater.inflate(R.layout.item_message_user, parent, false)
                : inflater.inflate(R.layout.item_message_bot, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.tvMessage.setText(message.getText());
        holder.tvTimestamp.setText(message.getFormattedTime());

        // Mensaje del usuario
        if (message.isFromUser()) {
            holder.tvMessage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.md_theme_on_primary));

            holder.tvMessage.setPaintFlags(holder.tvMessage.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            holder.tvMessage.setOnClickListener(null);
        } else {
            // Mensaje del bot
            if (message.getUrl() != null && !message.getUrl().isEmpty()) {
                holder.tvMessage.setTextColor(Color.BLUE);
                holder.tvMessage.setPaintFlags(holder.tvMessage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                holder.tvMessage.setOnClickListener(v -> {
                    String url = "http://10.0.2.2:8081" + message.getUrl();
                    Log.d("DEBUG", "URL al hacer clic: " + url);
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(url), "application/pdf");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        v.getContext().startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(v.getContext(), "No se pudo abrir el documento", Toast.LENGTH_SHORT).show();
                        Log.e("DEBUG", "Error al abrir la URL: " + e.getMessage());
                    }
                });
            } else {
                holder.tvMessage.setTextColor(Color.BLACK); // Normal sin URL
                holder.tvMessage.setPaintFlags(holder.tvMessage.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                holder.tvMessage.setOnClickListener(null);
            }
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTimestamp;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
