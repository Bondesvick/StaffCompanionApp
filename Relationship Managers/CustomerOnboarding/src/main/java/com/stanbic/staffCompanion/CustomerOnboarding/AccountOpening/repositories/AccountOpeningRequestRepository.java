package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.AccountOpeningRequestEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.SanctionScreeningOutcomeEnum;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.enums.SanctionScreeningStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountOpeningRequestRepository extends JpaRepository<AccountOpeningRequestEntity, String> {

    ArrayList<AccountOpeningRequestEntity> findByRelationshipManagerSAPIdEquals(String relationshipManagerSAPId);

    @Modifying
    @Query("update AccountOpeningRequestEntity a set a.sanctionScreeningOutcome = :sanctionScreeningOutcome, " +
            "a.sanctionScreeningStatus = :sanctionScreeningStatus, " +
            "a.onboardingHasBeenTerminated = :terminateOnboarding, " +
            "a.reasonForOnboardingTermination = :reasonForTermination " +
            "where a.accountOpeningRequestId = :AORequestID")
    Integer updateSanctionScreeningInformation(
            @Param("sanctionScreeningOutcome") SanctionScreeningOutcomeEnum sanctionScreeningOutcome,
            @Param("sanctionScreeningStatus") SanctionScreeningStatusEnum sanctionScreeningStatus,
            @Param("terminateOnboarding") Boolean terminateOnboarding,
            @Param("reasonForTermination") String reasonForTermination,
            @Param("AORequestID") String accountOpeningRequestID
    );

//    @Query("SELECT * from AccountOpeningRequestEntity a where " +
//            "a.accountOpeningRequestId = :AORequestID AND " +
//            "a.onboardingHasBeenTerminated = :onboardingTerminationStatus")
//    List<AccountOpeningRequestEntity> findByRequestIdAndOnboardingTerminationStatus(
//            @Param("AORequestID") String accountOpeningRequestID,
//            @Param("onboardingTerminationStatus") Boolean onboardingTerminationStatus
//    );

    Optional<AccountOpeningRequestEntity>
    findByAccountOpeningRequestIdAndOnboardingHasBeenTerminated(String requestID,
                                                                Boolean onboardingHasBeenTerminated);
}
