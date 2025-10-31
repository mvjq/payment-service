package org.example.paymentservice.presentation.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
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
public class WebhookResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("url")
    private String url;

    @JsonProperty("description")
    private String description;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
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
