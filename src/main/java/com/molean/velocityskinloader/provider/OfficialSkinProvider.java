package com.molean.velocityskinloader.provider;

import com.molean.velocityskinloader.client.MojangClient;
import com.molean.velocityskinloader.config.SkinProviderConfig;
import com.molean.velocityskinloader.model.mojang.MojangSkin;
import com.molean.velocityskinloader.model.mojang.UUIDProfile;
import com.velocitypowered.api.util.GameProfile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OfficialSkinProvider implements SkinProvider {
    private static OfficialSkinProvider instance = new OfficialSkinProvider(MojangClient.instance());
    private static Map<String, OfficialSkinProvider> providerMap = new ConcurrentHashMap<>();

    public static OfficialSkinProvider instance() {
        return instance;
    }

    public static OfficialSkinProvider of(SkinProviderConfig skinProviderConfig) {
        String apiUrl = skinProviderConfig.getApiUrl();
        String sessionUrl = skinProviderConfig.getSessionUrl();
        
        if (apiUrl == null || sessionUrl == null) {
            return instance;
        }
        
        String key = apiUrl + "|" + sessionUrl;
        return providerMap.computeIfAbsent(key, k -> {
            MojangClient client = MojangClient.of(apiUrl, sessionUrl);
            return new OfficialSkinProvider(client);
        });
    }

    private final MojangClient mojangClient;

    private OfficialSkinProvider(MojangClient mojangClient) {
        this.mojangClient = mojangClient;
    }

    @Override
    public GameProfile.Property getProperty(String name) {
        try {
            UUIDProfile uuidByName = mojangClient.getUUIDByName(name);
            MojangSkin skinByUUIDProfile = mojangClient.getSkinByUUIDProfile(uuidByName);
            List<GameProfile.Property> propertyList = skinByUUIDProfile.getProperties();
            return propertyList.size() > 0 ? propertyList.get(0) : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDisplay() {
        return "Official";
    }
}
