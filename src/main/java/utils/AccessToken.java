package utils;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccessToken {
    private String accessToken;

    public AccessToken(@JsonProperty("accessToken") String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
