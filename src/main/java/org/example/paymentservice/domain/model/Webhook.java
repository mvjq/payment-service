package org.example.paymentservice.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Webhook {
    private UUID id;
    private String url;
    private String description;
    private String secret;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    
    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean canReceiveEvents() {
        return Boolean.TRUE.equals(this.isActive);
    }

}
