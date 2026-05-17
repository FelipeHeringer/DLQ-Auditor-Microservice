package com.fhcs.dlq.auditor.infrastructure.adapter.out.persistence.postgres.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fhcs.dlq.auditor.infrastructure.adapter.out.persistence.postgres.entity.DlqMessageEntity;

public interface DlqMessageJpaRepository extends JpaRepository<DlqMessageEntity, UUID> {}
