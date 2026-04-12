package com.coindcx.springclient.client.auth;

import com.coindcx.springclient.client.ApiException;
import com.coindcx.springclient.client.Pair;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Authentication implementation that computes an HMAC-SHA256 signature over the
 * actual request body (payload) and sets it as the X-AUTH-SIGNATURE header.
 * <p>
 * The {@code applyToParams} method is called by {@code ApiClient.updateParamsForAuth}
 * with the exact serialized bytes that will be sent over the wire, so the signature
 * is always consistent with the body CoinDCX receives.
 */
public class HmacSignatureAuth implements Authentication {

    private String apiSecret;

    public HmacSignatureAuth() {}

    public HmacSignatureAuth(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    @Override
    public void applyToParams(List<Pair> queryParams, Map<String, String> headerParams,
                              Map<String, String> cookieParams, String payload,
                              String method, URI uri) throws ApiException {
        if (apiSecret == null || apiSecret.isEmpty() || payload == null) {
            return;
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            headerParams.put("X-AUTH-SIGNATURE", sb.toString());
        } catch (Exception e) {
            throw new ApiException("Failed to compute HMAC-SHA256 signature: " + e.getMessage());
        }
    }
}
