package com.fhcs.dlq.auditor.infrastructure.adapter.out.persistence.postgres.repository;

import org.springframework.stereotype.Repository;

import com.fhcs.dlq.auditor.application.port.out.persistence.DlqMessageRepositoryPort;
import com.fhcs.dlq.auditor.core.domain.bo.DlqMessageBO;
import com.fhcs.dlq.auditor.infrastructure.adapter.out.persistence.postgres.entity.DlqMessageEntity;
import com.fhcs.dlq.auditor.infrastructure.adapter.out.persistence.postgres.jpa.DlqMessageJpaRepository;
import com.fhcs.dlq.auditor.infrastructure.adapter.out.persistence.postgres.mapper.DlqMessageMapper;

@Repository
public class DlqMessageRepositoryAdapter implements DlqMessageRepositoryPort {
    private final DlqMessageJpaRepository jpaRepository;

    public DlqMessageRepositoryAdapter(DlqMessageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public DlqMessageBO salvar(DlqMessageBO bo) {
        DlqMessageEntity entity = DlqMessageMapper.toEntity(bo);
        DlqMessageEntity salvo = jpaRepository.save(entity);
        return DlqMessageMapper.toBO(salvo);
    }
}
