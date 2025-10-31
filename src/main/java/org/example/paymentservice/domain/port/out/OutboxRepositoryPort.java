package org.example.paymentservice.domain.port.out;

import org.example.paymentservice.infrastructure.adapter.persistence.entity.OutboxEventEntity;

import java.util.List;

public class OutboxRepositoryPort {

    public List<OutboxEventEntity> findUnpublishedEvents(int batchSize) {
        return null;
    }

    public void save(OutboxEventEntity event) {

    }
}
