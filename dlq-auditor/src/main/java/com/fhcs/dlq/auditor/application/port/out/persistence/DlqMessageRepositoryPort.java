package com.fhcs.dlq.auditor.application.port.out.persistence;

import com.fhcs.dlq.auditor.core.domain.bo.DlqMessageBO;

public interface DlqMessageRepositoryPort {
    DlqMessageBO salvar(DlqMessageBO dlqMessageBO);
}