package com.fhcs.dlq.auditor.application.services;

import com.fhcs.dlq.auditor.application.port.in.service.DlqMessageServicePort;
import com.fhcs.dlq.auditor.application.port.out.persistence.DlqMessageRepositoryPort;
import com.fhcs.dlq.auditor.core.domain.bo.DlqMessageBO;
import com.fhcs.dlq.auditor.core.domain.bo.SqsOrderMessageBO;

import org.springframework.stereotype.Service;

// application/services/DlqMessageService.java
@Service
public class DlqMessageService implements DlqMessageServicePort {

    private final DlqMessageRepositoryPort dlqMessageRepositoryPort;

    public DlqMessageService(DlqMessageRepositoryPort dlqMessageRepositoryPort) {
        this.dlqMessageRepositoryPort = dlqMessageRepositoryPort;
    }

    @Override
    public void processarMensagem(SqsOrderMessageBO bo, String queueName) {
        final DlqMessageBO dlqMessageBO = new DlqMessageBO();

        try {
            dlqMessageBO.inicializar(queueName, bo.toString());
            dlqMessageBO.definirSeveridade(bo.calcularQuantidadeTotalProdutos());

        } catch (Exception e) {
            dlqMessageBO.inicializar(queueName, bo.toString());
            dlqMessageBO.definirSeveridade(0);
        }

        dlqMessageRepositoryPort.salvar(dlqMessageBO);
    }
}