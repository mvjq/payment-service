package org.example.paymentservice.presentation.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.paymentservice.domain.model.Webhook;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response body containing webhook details")
public class WebhookResponse {

    @JsonProperty("id")
    @Schema(description = "Unique identifier of the webhook", 
            example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @JsonProperty("url")
    @Schema(description = "The URL endpoint where webhook notifications will be sent", 
            example = "https://example.com/webhook")
    private String url;

    @JsonProperty("description")
    @Schema(description = "A description of the webhook's purpose", 
            example = "Payment notifications for order processing")
    private String description;

    @JsonProperty("is_active")
    @Schema(description = "Whether the webhook is active and receiving notifications", 
            example = "true")
    private Boolean isActive;

    @JsonProperty("created_at")
    @Schema(description = "Timestamp when the webhook was created", 
            example = "2025-10-31T10:15:30")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Schema(description = "Timestamp when the webhook was last updated", 
            example = "2025-10-31T14:20:45")
    private LocalDateTime updatedAt;

    public static WebhookResponse fromDomain(Webhook webhookEntity) {
        return WebhookResponse.builder()
                .id(webhookEntity.getId())
                .url(webhookEntity.getUrl())
                .description(webhookEntity.getDescription())
                .isActive(webhookEntity.getIsActive())
                .createdAt(webhookEntity.getCreatedAt())
                .updatedAt(webhookEntity.getUpdatedAt())
                .build();
    }
}
