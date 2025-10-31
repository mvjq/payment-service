package org.example.paymentservice.infrastructure.adapter.persistence.repository;

import org.example.paymentservice.infrastructure.adapter.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {
    @Query("SELECT o FROM OutboxEventEntity o WHERE o.published = false ORDER BY o.createdAt ASC")
    List<OutboxEventEntity> findUnpublishedEvents();
    @Query(value = "SELECT * FROM outbox_events " +
            "WHERE published = false " +
            "AND retry_count < :maxRetries " +
            "ORDER BY created_at ASC " +
            "LIMIT :limit",
            nativeQuery = true)
    List<OutboxEventEntity> findUnpublishedEventsWithRetryLimit(
            @Param("maxRetries") int maxRetries,
            @Param("limit") int limit
    );
}
