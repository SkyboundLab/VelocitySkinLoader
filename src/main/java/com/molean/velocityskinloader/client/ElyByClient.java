package com.molean.velocityskinloader.client;

import com.molean.velocityskinloader.model.mojang.MojangSkin;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ElyByClient extends ApiClient {

    private static final ElyByClient instance = new ElyByClient("http://skinsystem.ely.by");
    private static Map<String, ElyByClient> clientMap = new ConcurrentHashMap<>();

    public static ElyByClient instance() {
        return instance;
    }

    public static ElyByClient of(String baseUrl) {
        return clientMap.computeIfAbsent(baseUrl, ElyByClient::new);
    }

    private ElyByClient(String baseUrl) {
        super(baseUrl);
    }

    public MojangSkin getSignedTextures(String username) throws Exception {
        HttpResponse<String> response = get("/textures/signed/" + username);
        String body = response.body();
        return gson.fromJson(body, MojangSkin.class);
    }
}
