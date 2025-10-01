package com.molean.velocityskinloader.provider;

import com.molean.velocityskinloader.client.ElyByClient;
import com.molean.velocityskinloader.config.SkinProviderConfig;
import com.molean.velocityskinloader.model.mojang.MojangSkin;
import com.velocitypowered.api.util.GameProfile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ElyByProvider implements SkinProvider {
    private static ElyByProvider instance = new ElyByProvider(ElyByClient.instance());
    private static Map<String, ElyByProvider> providerMap = new ConcurrentHashMap<>();

    public static ElyByProvider instance() {
        return instance;
    }

    public static ElyByProvider of(SkinProviderConfig skinProviderConfig) {
        String url = skinProviderConfig.getUrl();
        
        if (url == null || url.isEmpty()) {
            return instance;
        }
        
        return providerMap.computeIfAbsent(url, k -> {
            ElyByClient client = ElyByClient.of(url);
            return new ElyByProvider(client);
        });
    }

    private final ElyByClient elyByClient;

    private ElyByProvider(ElyByClient elyByClient) {
        this.elyByClient = elyByClient;
    }

    @Override
    public GameProfile.Property getProperty(String name) {
        try {
            MojangSkin signedTextures = elyByClient.getSignedTextures(name);
            List<GameProfile.Property> properties = signedTextures.getProperties();
            
            // Find and return the "textures" property
            for (GameProfile.Property property : properties) {
                if ("textures".equals(property.getName())) {
                    return property;
                }
            }
            
            return properties.size() > 0 ? properties.get(0) : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDisplay() {
        return "Ely.by (" + elyByClient.getBase() + ")";
    }
}
