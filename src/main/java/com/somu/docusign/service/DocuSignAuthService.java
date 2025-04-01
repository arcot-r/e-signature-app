package com.somu.docusign.service;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.auth.OAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Service
public class DocuSignAuthService {

    @Value("${docusign.integration_key}")
    private String integrationKey;

    @Value("${docusign.user_id}")
    private String userId;

    @Value("${docusign.private_key}")
    private Resource privateKeyResource;

    @Value("${docusign.api_base_url}")
    private String apiBaseUrl;

    private String accessToken;

    public String getAccessToken() throws Exception {
        if (accessToken == null || isTokenExpired()) {
            accessToken = generateJwtToken();
        }
        return accessToken;
    }

    private String generateJwtToken() throws Exception {
        ApiClient apiClient = new ApiClient(apiBaseUrl);
        List<String> scopes = List.of("signature", "impersonation");

        String privateKey = Files.readString(privateKeyResource.getFile().toPath(), StandardCharsets.UTF_8);

        OAuth.OAuthToken oAuthToken = apiClient.requestJWTUserToken(
            integrationKey, userId, scopes, privateKey.getBytes(), 3600
        );

        return oAuthToken.getAccessToken();
    }

    private boolean isTokenExpired() {
        // Add logic to check if token is expired (e.g., use a timestamp)
        return false;
    }
}
