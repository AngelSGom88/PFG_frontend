package com.example.pfg_frontend;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/api/respuesta")
    Call<RespuestaChat> obtenerRespuesta(@Query("mensaje") String mensaje);
}
