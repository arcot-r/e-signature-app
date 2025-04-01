package com.somu.docusign.service;
import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.Configuration;
import com.docusign.esign.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class DocuSignService {

    @Value("${docusign.api_base_url}")
    private String apiBaseUrl;

    @Value("${docusign.integration_key}")
    private String integrationKey;

    @Value("${docusign.user_id}")
    private String userId;

    private final DocuSignAuthService authService;

    public DocuSignService(DocuSignAuthService authService) {
        this.authService = authService;
    }

    public String sendDocument(String recipientEmail, String recipientName, String filePath) throws Exception {
        ApiClient apiClient = new ApiClient(apiBaseUrl);
        String accessToken = authService.getAccessToken();
        apiClient.setAccessToken(accessToken, 3600L);
        Configuration.setDefaultApiClient(apiClient);

        EnvelopesApi envelopesApi = new EnvelopesApi();

        // Read file and encode in Base64
        byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
        String base64Content = Base64.getEncoder().encodeToString(fileBytes);

        // Create Document
        Document document = new Document();
        document.setDocumentBase64(base64Content);
        document.setName("Sample Document");
        document.setFileExtension("pdf");
        document.setDocumentId("1");

        // Create Signer
        Signer signer = new Signer();
        signer.setEmail(recipientEmail);
        signer.setName(recipientName);
        signer.setRecipientId("1");

        // Create Envelope
        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Please sign this document");
        envelopeDefinition.setDocuments(Collections.singletonList(document));
        envelopeDefinition.setRecipients(new Recipients().signers(Collections.singletonList(signer)));
        envelopeDefinition.setStatus("sent");

        EnvelopeSummary envelopeSummary = envelopesApi.createEnvelope(userId, envelopeDefinition);
        return envelopeSummary.getEnvelopeId();
    }
}
