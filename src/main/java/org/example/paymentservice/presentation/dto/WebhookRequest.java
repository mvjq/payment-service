package org.example.paymentservice.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class WebhookRequest {

    @NotBlank
    @JsonProperty("url")
    private String url;

    @Size(max = 255)
    @JsonProperty("description")
    private String description;

    @JsonProperty("secret")
    private String secret;

    @JsonProperty("is_active")
    private Boolean active;
}