package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.AccountOpeningRequestEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.CustomerInformationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Repository
public interface CustomerInformationRepository extends JpaRepository<CustomerInformationEntity, Long> {

    Optional<CustomerInformationEntity> findByAccountOpeningRequestEntityEquals(
            AccountOpeningRequestEntity AORequestEntity);

    @Modifying
    @Query("update CustomerInformationEntity c set c.mobileNumber = :mobileNumber, " +
            "c.emailAddress = :emailAddress, c.addressLine1 = :addressLine1, " +
            "c.addressLine2 = :addressLine2, c.addressLine3 = :addressLine3, " +
            "c.lgaOfResidence = :lgaOfResidence, c.cityOfResidence = :cityOfResidence, " +
            "c.stateOfResidence = :stateOfResidence, c.countryCode = :countryCode, " +
            "c.postalCode = :postalCode, c.occupancyStartDate = :occupancyStartDate, " +
            "c.nearestLandmark = :nearestLandmark, c.otherAddressInfo = :otherAddressInfo, " +
            "c.preferredBranchSolID = :preferredBranchSolID " +
            "where c.accountOpeningRequestEntity = :aoRequestEntity")
    Integer updateContactInfo(
            @Param("mobileNumber") String mobileNumber,
            @Param("emailAddress") String emailAddress,
            @Param("addressLine1") String addressLine1,
            @Param("addressLine2") String addressLine2,
            @Param("addressLine3") String addressLine3,
            @Param("lgaOfResidence") String lgaOfResidence,
            @Param("cityOfResidence") String cityOfResidence,
            @Param("stateOfResidence") String stateOfResidence,
            @Param("countryCode") String countryCode,
            @Param("postalCode") String postalCode,
            @Param("occupancyStartDate") LocalDate occupancyStartDate,
            @Param("nearestLandmark") String nearestLandmark,
            @Param("otherAddressInfo") String otherAddressInfo,
            @Param("preferredBranchSolID") String preferredBranchSolID,
            @Param("aoRequestEntity") AccountOpeningRequestEntity aoRequestEntity
    );

    @Modifying
    @Query("update CustomerInformationEntity c set c.nokLastName = :nokLastName, " +
            "c.nokFirstName = :nokFirstName, c.nokOtherNames = :nokOtherNames, " +
            "c.nokMobileNumber = :nokMobileNumber, c.nokEmailAddress = :nokEmailAddress, " +
            "c.nokRelationship = :nokRelationship, c.nokDateOfBirth = :nokDateOfBirth, " +
            "c.nokAddressStreetNumber = :nokAddressStreetNumber, " +
            "c.nokAddressLandmark = :nokAddressLandmark, " +
            "c.nokAddressStateOfResidence = :nokAddressStateOfResidence, " +
            "c.nokCityOfResidence = :nokCityOfResidence " +
            "where c.accountOpeningRequestEntity = :aoRequestEntity")
    Integer updateNOKInfo(
            @Param("nokLastName") String nokLastName,
            @Param("nokFirstName") String nokFirstName,
            @Param("nokOtherNames") String nokOtherNames,
            @Param("nokMobileNumber") String nokMobileNumber,
            @Param("nokEmailAddress") String nokEmailAddress,
            @Param("nokRelationship") String nokRelationship,
            @Param("nokDateOfBirth") Date nokDateOfBirth,
            @Param("nokAddressStreetNumber") String nokAddressStreetNumber,
            @Param("nokAddressLandmark") String nokAddressLandmark,
            @Param("nokAddressStateOfResidence") String nokAddressStateOfResidence,
            @Param("nokCityOfResidence") String nokCityOfResidence,
            @Param("aoRequestEntity") AccountOpeningRequestEntity aoRequestEntity
    );

    @Modifying
    @Query("update CustomerInformationEntity c set c.hasHeldProminentPosition = :hasHeldProminentPosition, " +
            "c.isPoliticallyExposed = :isPoliticallyExposed, " +
            "c.nameOfPoliticalConnection = :nameOfPoliticalConnection, " +
            "c.positionHeld = :positionHeld, c.relationshipWithPerson = :relationshipWithPerson " +
            "where c.accountOpeningRequestEntity = :aoRequestEntity")
    Integer updateCustomerOtherInfo(
            @Param("hasHeldProminentPosition") Boolean hasHeldProminentPosition,
            @Param("isPoliticallyExposed") Boolean isPoliticallyExposed,
            @Param("nameOfPoliticalConnection") String nameOfPoliticalConnection,
            @Param("positionHeld") String positionHeld,
            @Param("relationshipWithPerson") String relationshipWithPerson,
            @Param("aoRequestEntity") AccountOpeningRequestEntity aoRequestEntity
    );
}
