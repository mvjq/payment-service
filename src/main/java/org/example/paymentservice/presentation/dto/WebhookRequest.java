package org.example.paymentservice.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body for registering or updating a webhook")
public class WebhookRequest {

    @NotBlank
    @JsonProperty("url")
    @Schema(description = "The URL endpoint where webhook notifications will be sent", 
            example = "https://example.com/webhook", 
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    @Size(max = 255)
    @JsonProperty("description")
    @Schema(description = "A description of the webhook's purpose", 
            example = "Payment notifications for order processing", 
            maxLength = 255)
    private String description;

    @JsonProperty("secret")
    @Schema(description = "Secret key for webhook signature verification", 
            example = "whsec_a1b2c3d4e5f6")
    private String secret;

    @JsonProperty("is_active")
    @Schema(description = "Whether the webhook is active and should receive notifications", 
            example = "true", 
            defaultValue = "true")
    private Boolean active;
}