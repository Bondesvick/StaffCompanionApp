package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * Manages the digital Onboarding process, end-to-end
 *
 **/
@EnableScheduling
public class DigitalOnboardingOrchestrator {

    private Logger log = LoggerFactory.getLogger(DigitalOnboardingOrchestrator.class);

    @Autowired
    AccountGenerationService accountGenerationService;


    @Scheduled(fixedRate = 3600000)
    private void onboardCustomers(){
        // Get AO Requests that have passed all required checks & have all required data
        // For each request, generate a CIF for the customer
        // Once done, generate the account number
    }

    @Scheduled(fixedRate = 3600000)
    private void moveDocsToDSX(){
        // Get accounts whose docs need to be pushed to DSX
        // Generate the documents
        // Push the document to DSX
    }


    @Scheduled(fixedRate = 3600000)
    private void pushMandateDocsToFinacle(){
        //
    }


    @Scheduled(fixedRate = 3600000)
    private void notifyStakeholdersOfOnboardingCompletion(){
        // Get AO Requests that have been fulfilled
        // Send emails/SMS to customer
        // Send email to RM
        // Send email to bank branch
    }
}
