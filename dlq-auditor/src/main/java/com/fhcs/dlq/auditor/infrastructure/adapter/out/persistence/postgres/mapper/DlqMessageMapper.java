package com.fhcs.dlq.auditor.infrastructure.adapter.out.persistence.postgres.mapper;

import com.fhcs.dlq.auditor.core.domain.bo.DlqMessageBO;
import com.fhcs.dlq.auditor.infrastructure.adapter.out.persistence.postgres.entity.DlqMessageEntity;

public class DlqMessageMapper {
    private DlqMessageMapper() {}

    public static DlqMessageEntity toEntity(DlqMessageBO bo) {
        DlqMessageEntity entity = new DlqMessageEntity();
        entity.setErrorId(bo.getErrorId());
        entity.setQueueName(bo.getQueueName());
        entity.setPayload(bo.getPayload());
        entity.setTimestamp(bo.getTimestamp());
        entity.setStatus(bo.getStatus());
        entity.setSeverity(bo.getSeverity());
        return entity;
    }

    public static DlqMessageBO toBO(DlqMessageEntity entity) {
        DlqMessageBO bo = new DlqMessageBO();
        bo.setErrorId(entity.getErrorId());
        bo.setQueueName(entity.getQueueName());
        bo.setPayload(entity.getPayload());
        bo.setTimestamp(entity.getTimestamp());
        bo.setStatus(entity.getStatus());
        bo.setSeverity(entity.getSeverity());
        return bo;
    }
}

