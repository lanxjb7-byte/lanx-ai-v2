
package com.lanxx.ai;

import okhttp3.*;
import org.json.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class AIClient {

    private static final String ENDPOINT =
        "https://lanx-ai-v2.netlify.app/.netlify/functions/chat";

    private final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build();

    public interface Callback {
        void onSuccess(String answer);
        void onError(String error);
    }

    public void send(String question, boolean hardWork, Callback callback) {

        try {
            JSONArray messages = new JSONArray();

            JSONObject user = new JSONObject();
            user.put("role", "user");
            user.put("content", question);
            messages.put(user);

            JSONObject payload = new JSONObject();
            payload.put("messages", messages);
            payload.put("hard_work", hardWork);

            RequestBody body = RequestBody.create(
                payload.toString(),
                MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                .url(ENDPOINT)
                .post(body)
                .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response)
                    throws IOException {

                    String result = response.body().string();

                    if (!response.isSuccessful()) {
                        callback.onError("Server: " + response.code());
                        return;
                    }

                    try {
                        JSONObject json = new JSONObject(result);
                        String answer = json
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");

                        callback.onSuccess(answer);

                    } catch (Exception e) {
                        callback.onError("Response AI tidak valid");
                    }
                }
            });

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
              }
