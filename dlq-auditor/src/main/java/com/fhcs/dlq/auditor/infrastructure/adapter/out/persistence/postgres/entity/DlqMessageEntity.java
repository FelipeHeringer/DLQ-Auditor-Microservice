package com.fhcs.dlq.auditor.infrastructure.adapter.out.persistence.postgres.entity;

import com.fhcs.dlq.auditor.core.domain.bo.enums.Severity;
import com.fhcs.dlq.auditor.core.domain.bo.enums.AuditStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tb_dlq_message")
public class DlqMessageEntity {

    @Id
    private UUID errorId;

    private String queueName;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    private AuditStatus status;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    public UUID getErrorId() { return errorId; }
    public void setErrorId(UUID errorId) { this.errorId = errorId; }
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public AuditStatus getStatus() { return status; }
    public void setStatus(AuditStatus status) { this.status = status; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
}