package org.example.paymentservice.domain.port.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookCommand {
    @NotBlank
    private String url;

    @Size(max = 255)
    private String description;

    private String secret;
}