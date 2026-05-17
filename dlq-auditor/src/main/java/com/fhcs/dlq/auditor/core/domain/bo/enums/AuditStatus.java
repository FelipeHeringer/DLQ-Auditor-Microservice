package com.fhcs.dlq.auditor.core.domain.bo.enums;

public enum AuditStatus {

    // Record created, waiting to be picked up for analysis
    PENDING_ANALYSIS,

    // Analysis is currently being performed
    UNDER_ANALYSIS,

    // Analysis completed, waiting for a formal conclusion
    PENDING_CONCLUSION,

    // Failure was identified and documented
    CONCLUDED,

    // Record flagged due to compliance or severity concerns
    ESCALATED,

    // Audit finalized, no further action required
    CLOSED,

    // Record was invalidated (e.g. duplicate, irrelevant message)
    DISCARDED;
}