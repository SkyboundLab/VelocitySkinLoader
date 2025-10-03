package com.molean.velocityskinloader.client;

import com.molean.velocityskinloader.model.mojang.MojangSkin;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SkySkinsClient extends ApiClient {

    private static final SkySkinsClient instance = new SkySkinsClient("https://skyskin.twint.my.id");
    private static Map<String, SkySkinsClient> clientMap = new ConcurrentHashMap<>();

    public static SkySkinsClient instance() {
        return instance;
    }

    public static SkySkinsClient of(String baseUrl) {
        return clientMap.computeIfAbsent(baseUrl, SkySkinsClient::new);
    }

    private SkySkinsClient(String baseUrl) {
        super(baseUrl);
    }

    public MojangSkin getSignedTextures(String uuid) throws Exception {
        HttpResponse<String> response = get("/textures/signed/" + uuid);
        String body = response.body();
        return gson.fromJson(body, MojangSkin.class);
    }
}
