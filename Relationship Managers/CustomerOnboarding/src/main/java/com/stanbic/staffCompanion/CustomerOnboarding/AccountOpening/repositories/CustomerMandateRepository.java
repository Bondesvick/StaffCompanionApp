package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.AccountOpeningRequestEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.CustomerMandateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface CustomerMandateRepository extends JpaRepository<CustomerMandateEntity, Long> {

    Optional<ArrayList<CustomerMandateEntity>> findByAccountOpeningRequestEquals(
            AccountOpeningRequestEntity AORequestEntity);
}
