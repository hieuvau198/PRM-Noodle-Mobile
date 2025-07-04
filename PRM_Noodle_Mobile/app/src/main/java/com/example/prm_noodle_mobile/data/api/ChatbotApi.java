package com.example.prm_noodle_mobile.data.api;

import com.example.prm_noodle_mobile.data.model.ChatMessage;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatbotApi {
    @POST("/api/Chat/message")
    Call<Object> sendMessage(@Body ChatMessage chatMessage);
} 