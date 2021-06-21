package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.SanctionScreeningResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SanctionScreeningResultRepository
        extends JpaRepository<SanctionScreeningResultEntity, Long> {
}
