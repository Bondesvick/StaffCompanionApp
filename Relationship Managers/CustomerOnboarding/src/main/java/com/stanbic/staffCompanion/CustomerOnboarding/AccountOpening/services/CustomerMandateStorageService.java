package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIRequests.customerInfoUpload.CustomerMandateInfo;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.AccountOpeningRequestEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.CustomerMandateEntity;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions.CustomerMandateStorageException;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.repositories.CustomerMandateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomerMandateStorageService {

    private Logger log = LoggerFactory.getLogger(CustomerMandateStorageService.class);

    @Autowired
    private CustomerMandateRepository mandateRepository;

    public void storeMandateFile(
            CustomerMandateInfo customerMandateInfo,
            AccountOpeningRequestEntity accountOpeningRequest) throws CustomerMandateStorageException {

        try {
            CustomerMandateEntity mandateEntity;
            mandateEntity = new CustomerMandateEntity(
                    customerMandateInfo.getFileName(),
                    customerMandateInfo.getFileFormat(),
                    customerMandateInfo.getDocumentType(),
                    customerMandateInfo.getBase64Data(),
                    accountOpeningRequest);

            mandateRepository.save(mandateEntity);

        } catch (Exception e) {
            throw new CustomerMandateStorageException("Unable to store customer mandate. Please try again later.");
        }

    }

    public void getMandateDocuments(
            AccountOpeningRequestEntity accountOpeningRequest) {

        Optional<ArrayList<CustomerMandateEntity>> mandateDocuments;
        mandateDocuments = this.mandateRepository.findByAccountOpeningRequestEquals(
                accountOpeningRequest
        );

        if (mandateDocuments.isPresent()){
            log.info("Mandate documents are present");
            System.out.println(mandateDocuments.get().toString());
        } else {
            log.info("No mandate documents");
        }

    }
}
