package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.accountGeneration.RetailCIFGenRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.accountGeneration.CurrentAccountGenRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.accountGeneration.SavingsAccountGenRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RequestManagerRequest;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.redboxInterface.requestManager.RequestManagerResponse;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions.AccountGenerationServiceException;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.utils.RedboxRequestManagerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountGenerationService {

    private Logger log = LoggerFactory.getLogger(AccountGenerationService.class);

    @Autowired
    private RedboxRequestManagerClient redboxRequestManagerClient;

    /** Generate the Customer ID for a new-to-bank customer **/
    public void generateRetailCif(RetailCIFGenRequest retailCifGenRequest) throws AccountGenerationServiceException {

        // Generate the xml request
        RequestManagerRequest requestManagerRequest = new RequestManagerRequest();

        // Setup body
        requestManagerRequest.setChannel("BPM");
        requestManagerRequest.setType("CIF_CREATION");
        requestManagerRequest.setBody(this.setupRetailCIFGenRequestBody(retailCifGenRequest));

        RequestManagerResponse requestManagerResponse;

        // Send request to redbox
        try{
            log.info("Sending customer details to Redbox for CIF generation");

            requestManagerResponse = redboxRequestManagerClient.callRequestManagerService(requestManagerRequest);

        }catch (Exception e){
            log.error(e.getMessage());

            throw new AccountGenerationServiceException(
                    "Unable to generate CIF for customer"
            );
        }

        switch (requestManagerResponse.getResponseCode()){
            case "000":
                // TODO
                //  CIF Generated successfully
                //  what next?
                //  return CIF? What else should be returned
                return;
            case "999":
            default:
                throw new AccountGenerationServiceException(
                        "Unable to generate CIF for customer"
                );
        }
    }

    private String setupRetailCIFGenRequestBody(RetailCIFGenRequest retailCifGenRequest){
        String requestBody =
                "<soap:doCustomerCreation xmlns:soap=\"http://soap.finacle.redbox.stanbic.com/\">"+
                        "<ModuleTranReferenceId></ModuleTranReferenceId>"+
                        "<CustomerCreationRequest>"+
                            "<CustomerTypeCode>"+retailCifGenRequest.getCustomerTypeCode()+"</CustomerTypeCode>"+
                            "<Salutation>"+retailCifGenRequest.getSalutationCode()+"</Salutation>"+
                            "<FirstName>"+retailCifGenRequest.getFirstName()+"</FirstName>"+
                            "<MiddleName>"+retailCifGenRequest.getMiddleName()+"</MiddleName>"+
                            "<LastName>"+retailCifGenRequest.getLastName()+"</LastName>"+
                            "<PreferredName>"+retailCifGenRequest.getPreferredName()+"</PreferredName>"+
                            "<ContactAddressDetails>"+
                                "<PhoneEmailInfo>"+
                                    "<PhoneEmailType>WORKEML</PhoneEmailType>"+
                                    "<IsPhoneInfo>0</IsPhoneInfo>"+
                                    "<PreferredFlag>1</PreferredFlag>"+
                                    "<Email>"+retailCifGenRequest.getEmailAddress()+"</Email>"+
                                "</PhoneEmailInfo>"+
                                "<PhoneEmailInfo>"+
                                    "<PhoneEmailType>CELLPH</PhoneEmailType>"+
                                    "<IsPhoneInfo>1</IsPhoneInfo>"+
                                    "<PreferredFlag>1</PreferredFlag>"+
                                    "<PhoneNumber>"+retailCifGenRequest.getPhoneNumber()+"</PhoneNumber>"+
                                    "<PhoneNumberCountryCode></PhoneNumberCountryCode>"+
                                    "<PhoneNumberCityCode></PhoneNumberCityCode>"+
                                    "<PhoneNumberLocalCode></PhoneNumberLocalCode>"+
                                "</PhoneEmailInfo>"+
                            "</ContactAddressDetails>"+
                            "<PhysicalAddressDetails>"+
                                "<DefaultAddressType>Home</DefaultAddressType>"+
                                "<HomeAddressDetails>"+
                                    "<addressCategory>Home</addressCategory>"+
                                    "<addressLine1>"+retailCifGenRequest.getAddressLine1()+"</addressLine1>"+
                                    "<city>"+retailCifGenRequest.getCity()+"</city>"+
                                    "<country>"+retailCifGenRequest.getCountryCode()+"</country>"+
                                    "<freeTextLabel>Suites</freeTextLabel>"+
                                    "<isPreferredAddr>1</isPreferredAddr>"+
                                    "<postalCode>"+retailCifGenRequest.getPostalCode()+"</postalCode>"+
                                    "<preferredFormat>FREE_TEXT_FORMAT</preferredFormat>"+
                                    "<startDate>"+retailCifGenRequest.getOccupancyStartDate()+"</startDate>"+
                                    "<state>"+retailCifGenRequest.getState()+"</state>"+
                                "</HomeAddressDetails>"+
                            "</PhysicalAddressDetails>"+
                            "<BirthDate>"+retailCifGenRequest.getBirthDate()+"</BirthDate>"+
                            "<BirthMonth>"+retailCifGenRequest.getBirthMonth()+"</BirthMonth>"+
                            "<BirthYear>"+retailCifGenRequest.getBirthYear()+"</BirthYear>"+
                            "<Gender>"+retailCifGenRequest.getGender()+"</Gender>"+
                            "<CustomerBVN>"+retailCifGenRequest.getCustomerBvn()+"</CustomerBVN>"+
                            "<BVNEnrollmentBank></BVNEnrollmentBank>"+
                            "<BVNEnrollmentBranch></BVNEnrollmentBranch>"+
                            "<IsCustomerNonResident>"+retailCifGenRequest.getIsCustomerNonResident()+"</IsCustomerNonResident>"+
                            "<IsCustomerMinor>"+retailCifGenRequest.getIsCustomerMinor()+"</IsCustomerMinor>"+
                            "<Occupation>"+retailCifGenRequest.getOccupationCode()+"</Occupation>"+
                            "<IsStaffFlag>0</IsStaffFlag>"+
                            "<StaffEmployeeId/>"+
                            "<SegmentationClass>"+retailCifGenRequest.getSegmentationClassCode()+"</SegmentationClass>"+
                            "<SubSegment>"+retailCifGenRequest.getSubSegmentCode()+"</SubSegment>"+
                            "<Manager>"+retailCifGenRequest.getRelationshipManagerSAPID()+"</Manager>"+
                            "<NativeLanguageCode>INFENG</NativeLanguageCode>"+
                            "<Language>en</Language>"+
                            "<RelationshipOpeningDate>"+ retailCifGenRequest.getRelationshipOpeningDate()+"</RelationshipOpeningDate>"+
                            "<TaxDeductionTable>001</TaxDeductionTable>"+
                            "<CreatedBySystemId>1</CreatedBySystemId>"+
                            "<MaritalStatus>"+retailCifGenRequest.getMaritalStatusCode()+"</MaritalStatus>"+
                            "<Nationality>"+retailCifGenRequest.getNationality()+"</Nationality>"+
                            "<EmploymentStatus>"+retailCifGenRequest.getEmploymentStatusCode()+"</EmploymentStatus>"+
                            "<CustomerBranchId>"+retailCifGenRequest.getCustomerBranchSolId()+"</CustomerBranchId>"+
                            "<SupportingDocumentData>"+
                                "<CountryOfIssue>"+retailCifGenRequest.getIDCountryOfIssue()+"</CountryOfIssue>"+
                                "<DocumentCode>"+retailCifGenRequest.getIDDocumentCode()+"</DocumentCode>"+
                                "<IssueDate>"+retailCifGenRequest.getIDIssueDate()+"</IssueDate>"+
                                "<TypeCode>"+retailCifGenRequest.getIDTypeCode()+"</TypeCode>"+
                                "<TypeDesc>"+retailCifGenRequest.getIDTypeDescription()+"</TypeDesc>"+
                                "<PlaceOfIssue>"+retailCifGenRequest.getIDPlaceOfIssue()+"</PlaceOfIssue>"+
                                "<ReferenceNumber></ReferenceNumber>"+
                                "<DocumentIssuer>"+retailCifGenRequest.getIDDocumentIssuer()+"</DocumentIssuer>"+
                            "</SupportingDocumentData>"+
                            "<IsCustomerCoreProfileActive>1</IsCustomerCoreProfileActive>"+
                        "</CustomerCreationRequest>"+
                        "<ChannelIdCode>0</ChannelIdCode>"+
                        "</soap:doCustomerCreation>";

        return requestBody;
    }

    private void processCIFGenerationResults(){  }

    /** Generate a savings account for a customer **/
    public void generateSavingsAccount(SavingsAccountGenRequest savingsAccountGenRequest){

    }

    /** Generate a current account for a customer **/
    public void generateCurrentAccount(CurrentAccountGenRequest currentAccountGenRequest){

    }
}
