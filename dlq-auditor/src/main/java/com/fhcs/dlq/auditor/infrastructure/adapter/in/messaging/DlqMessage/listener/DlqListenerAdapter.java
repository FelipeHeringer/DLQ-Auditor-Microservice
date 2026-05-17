package com.fhcs.dlq.auditor.infrastructure.adapter.in.messaging.DlqMessage.listener;

import com.fhcs.dlq.auditor.application.port.in.service.DlqMessageServicePort;
import com.fhcs.dlq.auditor.core.domain.bo.SqsOrderMessageBO;
import com.fhcs.dlq.auditor.infrastructure.adapter.in.messaging.DlqMessage.dto.SqsOrderMessageDTO;
import com.fhcs.dlq.auditor.infrastructure.adapter.in.messaging.DlqMessage.mapper.SqsOrderMessageMapper;

import io.awspring.cloud.sqs.annotation.SqsListener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DlqListenerAdapter {

    @Value("${aws.sqs.dlq-queue-name}")
    private String QUEUE_NAME;

    private final DlqMessageServicePort dlqMessageServicePort;

    public DlqListenerAdapter(DlqMessageServicePort dlqMessageServicePort) {
        this.dlqMessageServicePort = dlqMessageServicePort;
    }

    @SqsListener("${aws.sqs.dlq-queue-name}")
    public void receberMensagem(SqsOrderMessageDTO dto) {
        SqsOrderMessageBO bo = SqsOrderMessageMapper.toBO(dto);

        dlqMessageServicePort.processarMensagem(bo, QUEUE_NAME);
    }
}