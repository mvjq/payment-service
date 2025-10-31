package org.example.paymentservice.domain.port.out;

import org.example.paymentservice.domain.event.DomainEvent;

public interface EventStorePort {
    void store(DomainEvent event);
}
