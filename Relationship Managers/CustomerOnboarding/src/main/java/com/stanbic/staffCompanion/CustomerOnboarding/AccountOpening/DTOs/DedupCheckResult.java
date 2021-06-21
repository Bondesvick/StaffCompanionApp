package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs;

public class DedupCheckResult {

    private String accountOpeningRequestID;
    private final Boolean matchExists;
    private String finacleCustomerId;
    private String finacleCustomerType;
    private String finacleCustomerLastName;
    private String finacleCustomerMiddleName;
    private String finacleCustomerFirstName;

    public DedupCheckResult(Boolean matchExists) {
        this.matchExists = matchExists;
    }

    public String getAccountOpeningRequestID() {
        return accountOpeningRequestID;
    }

    public void setAccountOpeningRequestID(String accountOpeningRequestID) {
        this.accountOpeningRequestID = accountOpeningRequestID;
    }

    public Boolean getMatchExists() {
        return matchExists;
    }

    public String getFinacleCustomerId() {
        return finacleCustomerId;
    }

    public void setFinacleCustomerId(String finacleCustomerId) {
        this.finacleCustomerId = finacleCustomerId;
    }

    public String getFinacleCustomerLastName() {
        return finacleCustomerLastName;
    }

    public void setFinacleCustomerLastName(String finacleCustomerLastName) {
        this.finacleCustomerLastName = finacleCustomerLastName;
    }

    public String getFinacleCustomerMiddleName() {
        return finacleCustomerMiddleName;
    }

    public void setFinacleCustomerMiddleName(String finacleCustomerMiddleName) {
        this.finacleCustomerMiddleName = finacleCustomerMiddleName;
    }

    public String getFinacleCustomerFirstName() {
        return finacleCustomerFirstName;
    }

    public void setFinacleCustomerFirstName(String finacleCustomerFirstName) {
        this.finacleCustomerFirstName = finacleCustomerFirstName;
    }

    public String getFinacleCustomerType() {
        return finacleCustomerType;
    }

    public void setFinacleCustomerType(String finacleCustomerType) {
        this.finacleCustomerType = finacleCustomerType;
    }

    @Override
    public String toString() {
        return "DedupCheckResultDTO{" +
                "accountOpeningRequestID='" + accountOpeningRequestID + '\'' +
                ", customerExists=" + matchExists +
                ", customerId='" + finacleCustomerId + '\'' +
                ", customerType='" + finacleCustomerType + '\'' +
                ", customerLastName='" + finacleCustomerLastName + '\'' +
                ", customerMiddleName='" + finacleCustomerMiddleName + '\'' +
                ", customerFirstName='" + finacleCustomerFirstName + '\''+
                '}';
    }
}
