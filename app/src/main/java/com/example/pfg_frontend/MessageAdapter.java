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

        if (message.getUrl() != null && !message.getUrl().trim().isEmpty()) {
            holder.tvMessage.setTextColor(Color.BLUE);
            holder.tvMessage.setPaintFlags(holder.tvMessage.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            holder.tvMessage.setOnClickListener(v -> {
                String url = "http://192.168.56.1:8081" + message.getUrl();  // Asegúrate que la URL esté completa
                Log.d("DEBUG", "URL al hacer clic: " + url);

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    v.getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(v.getContext(), "No se pudo abrir el navegador", Toast.LENGTH_SHORT).show();
                    Log.e("DEBUG", "Error al abrir el navegador: " + e.getMessage());
                }
            });

        } else {
            holder.tvMessage.setTextColor(Color.BLACK);
            holder.tvMessage.setPaintFlags(holder.tvMessage.getPaintFlags() & ~Paint.UNDERLINE_TEXT_FLAG);
            holder.tvMessage.setOnClickListener(null);
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
