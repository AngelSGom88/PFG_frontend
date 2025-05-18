package com.example.pfg_frontend;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatFragment extends Fragment {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private MaterialButton btnSend;
    private MessageAdapter adapter;
    private List<Message> messageList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chat, container, false);

        // Referenciar vistas
        rvMessages = root.findViewById(R.id.rvMessages);
        etMessage = root.findViewById(R.id.etMessage);
        btnSend = root.findViewById(R.id.btnSend);

        // Configurar RecyclerView
        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList);
        rvMessages.setAdapter(adapter);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));

        // Enviar mensaje
        btnSend.setOnClickListener(v -> {
            String mensajeUsuario = etMessage.getText().toString().trim();

            if (!mensajeUsuario.isEmpty()) {
                // Añadir mensaje del usuario
                messageList.add(new Message(mensajeUsuario, true, System.currentTimeMillis()));
                adapter.notifyItemInserted(messageList.size() - 1);
                etMessage.setText("");

                // Retrofit
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.56.1:8081") // cambia si usas móvil físico
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                ApiService apiService = retrofit.create(ApiService.class);
                Call<RespuestaChat> call = apiService.obtenerRespuesta(mensajeUsuario);

                call.enqueue(new Callback<RespuestaChat>() {
                    @Override
                    public void onResponse(Call<RespuestaChat> call, Response<RespuestaChat> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RespuestaChat respuesta = response.body();  // ✅ esta línea faltaba

                            // Añadir respuesta principal del bot
                            String textoRespuesta = respuesta.getRespuesta();
                            messageList.add(new Message(textoRespuesta, false, System.currentTimeMillis()));
                            adapter.notifyItemInserted(messageList.size() - 1);

                            // Si viene un documento, lo mostramos como mensaje clicable
                            if (respuesta.getUrl() != null && !respuesta.getUrl().isEmpty()) {
                                String textoDocumento = "📄 Documento: " + respuesta.getNombre() + "\n👉 Pulsa para abrir";
                                Message mensajeConLink = new Message(textoDocumento, false, System.currentTimeMillis());
                                mensajeConLink.setUrl(respuesta.getUrl()); // Solo si agregaste get/setUrl() en Message
                                messageList.add(mensajeConLink);
                                adapter.notifyItemInserted(messageList.size() - 1);
                            }

                            rvMessages.scrollToPosition(messageList.size() - 1);
                        } else {
                            messageList.add(new Message("❌ Error del servidor", false, System.currentTimeMillis()));
                            adapter.notifyItemInserted(messageList.size() - 1);
                        }
                    }

                    @Override
                    public void onFailure(Call<RespuestaChat> call, Throwable t) {
                        messageList.add(new Message("⚠️ Error de red: " + t.getMessage(), false, System.currentTimeMillis()));
                        adapter.notifyItemInserted(messageList.size() - 1);
                    }
                });
            }
        });

        return root;
    }

    private void simulateBotResponse() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            messageList.add(new Message("Hola, soy tu chatbot 🤖", false, System.currentTimeMillis()));
            adapter.notifyItemInserted(messageList.size() - 1);
            rvMessages.scrollToPosition(messageList.size() - 1);
        }, 1000);
    }
}
