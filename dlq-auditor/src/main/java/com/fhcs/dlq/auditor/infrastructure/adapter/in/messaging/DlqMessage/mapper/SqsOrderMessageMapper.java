package com.fhcs.dlq.auditor.infrastructure.adapter.in.messaging.DlqMessage.mapper;

import com.fhcs.dlq.auditor.core.domain.bo.OrderItemBO;
import com.fhcs.dlq.auditor.core.domain.bo.SqsOrderMessageBO;
import com.fhcs.dlq.auditor.infrastructure.adapter.in.messaging.DlqMessage.dto.SqsOrderMessageDTO;

import java.util.List;
import java.util.stream.Collectors;

public class SqsOrderMessageMapper {

    private SqsOrderMessageMapper() {}

    public static SqsOrderMessageBO toBO(SqsOrderMessageDTO dto) {
        final SqsOrderMessageBO bo = new SqsOrderMessageBO();

        bo.setZipCode(dto.getZipCode());
        bo.setCustomerId(dto.getCustomerId());
        bo.setOrigin(dto.getOrigin());
        bo.setOccurredAt(dto.getOccurredAt());

        if (dto.getOrderItems() != null) {
            final List<OrderItemBO> itens = dto.getOrderItems().stream()
                    .map(itemDTO -> {
                        final OrderItemBO itemBO = new OrderItemBO();
                        itemBO.setSku(itemDTO.getSku());
                        itemBO.setAmount(itemDTO.getAmount());
                        return itemBO;
                    })
                    .collect(Collectors.toList());
            bo.setOrderItems(itens);
        }

        return bo;
    }
}