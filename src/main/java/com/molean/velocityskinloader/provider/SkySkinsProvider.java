package com.molean.velocityskinloader.provider;

import com.molean.velocityskinloader.client.SkySkinsClient;
import com.molean.velocityskinloader.config.SkinProviderConfig;
import com.molean.velocityskinloader.model.mojang.MojangSkin;
import com.velocitypowered.api.util.GameProfile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SkySkinsProvider implements SkinProvider {
    private static SkySkinsProvider instance = new SkySkinsProvider(SkySkinsClient.instance());
    private static Map<String, SkySkinsProvider> providerMap = new ConcurrentHashMap<>();

    public static SkySkinsProvider instance() {
        return instance;
    }

    public static SkySkinsProvider of(SkinProviderConfig skinProviderConfig) {
        String url = skinProviderConfig.getUrl();
        
        if (url == null || url.isEmpty()) {
            return instance;
        }
        
        return providerMap.computeIfAbsent(url, k -> {
            SkySkinsClient client = SkySkinsClient.of(url);
            return new SkySkinsProvider(client);
        });
    }

    private final SkySkinsClient skySkinsClient;

    private SkySkinsProvider(SkySkinsClient skySkinsClient) {
        this.skySkinsClient = skySkinsClient;
    }

    @Override
    public GameProfile.Property getProperty(String uuid) {
        try {
            MojangSkin signedTextures = skySkinsClient.getSignedTextures(uuid);
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
        return "SkySkins (" + skySkinsClient.getBase() + ")";
    }
}
