package com.molean.velocityskinloader.client;

import com.molean.velocityskinloader.model.mojang.MojangSkin;
import com.molean.velocityskinloader.model.mojang.UUIDProfile;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MojangClient extends ApiClient {

    private static final MojangClient instance = new MojangClient("https://api.mojang.com", "https://sessionserver.mojang.com");
    private static Map<String, MojangClient> clientMap = new ConcurrentHashMap<>();

    public static MojangClient instance() {
        return instance;
    }

    public static MojangClient of(String apiUrl, String sessionUrl) {
        String key = apiUrl + "|" + sessionUrl;
        return clientMap.computeIfAbsent(key, k -> new MojangClient(apiUrl, sessionUrl));
    }

    private final String apiUrl;
    private final String sessionUrl;

    private MojangClient(String apiUrl, String sessionUrl) {
        super("https://");
        this.apiUrl = apiUrl;
        this.sessionUrl = sessionUrl;
    }

    public UUIDProfile getUUIDByName(String name) throws Exception {
        HttpResponse<String> response = get(apiUrl + "/users/profiles/minecraft/" + name);
        String body = response.body();
        return gson.fromJson(body, UUIDProfile.class);
    }

    public MojangSkin getSkinByUUIDProfile(UUIDProfile uuidProfile) throws Exception {
        HttpResponse<String> response = get(sessionUrl + "/session/minecraft/profile/" + uuidProfile.getId());
        String body = response.body();
        return gson.fromJson(body, MojangSkin.class);
    }
}
