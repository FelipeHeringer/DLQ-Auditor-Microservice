// core/domain/bo/DlqMessageBO.java
package com.fhcs.dlq.auditor.core.domain.bo;

import com.fhcs.dlq.auditor.core.domain.bo.enums.AuditStatus;
import com.fhcs.dlq.auditor.core.domain.bo.enums.Severity;

import java.time.Instant;
import java.util.UUID;

public class DlqMessageBO {

    private UUID errorId;
    private String queueName;
    private String payload;     
    private Instant timestamp;
    private AuditStatus status;
    private Severity severity;

    public void definirSeveridade(int quantidadeTotalProdutos) {
        if (quantidadeTotalProdutos > 100) {
            this.severity = Severity.HIGH;
        } else if (quantidadeTotalProdutos >= 50) {
            this.severity = Severity.MEDIUM;
        } else {
            this.severity = Severity.LOW;
        }
    }

    public void inicializar(String queueName, String payload) {
        this.errorId = UUID.randomUUID();
        this.queueName = queueName;
        this.payload = payload;
        this.timestamp = Instant.now();
        this.status = AuditStatus.PENDING_ANALYSIS;
    }

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