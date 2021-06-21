package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.AccountOpeningRequestEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.BVNValidationResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BVNValidationResultRepository extends JpaRepository<BVNValidationResultEntity, Long> {

    Optional<BVNValidationResultEntity> findByAccountOpeningRequestEquals(
            AccountOpeningRequestEntity AORequestEntity);
}
