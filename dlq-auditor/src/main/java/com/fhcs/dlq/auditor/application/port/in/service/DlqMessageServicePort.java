package com.fhcs.dlq.auditor.application.port.in.service;

import com.fhcs.dlq.auditor.core.domain.bo.SqsOrderMessageBO;

public interface DlqMessageServicePort {
    void processarMensagem(SqsOrderMessageBO bo);
}