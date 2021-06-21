package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.SanctionScreeningBotQueueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SanctionScreeningBotQueueRepository
        extends JpaRepository<SanctionScreeningBotQueueEntity, Long> {

}
