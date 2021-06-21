package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.exceptions;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIResponses.StandardAPIResponse;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.sanctionScreening.SanctionScreeningResultUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class APIExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(APIExceptionHandler.class);

    /** Handle exceptions thrown by the account opening service **/
    @ExceptionHandler(value = {AccountOpeningServiceException.class})
    public ResponseEntity<StandardAPIResponse> handleAOServiceException(
            AccountOpeningServiceException e){

        log.error(e.getMessage());

        HttpStatus httpStatus = this.getHttpStatusForAOException(e);

        final StandardAPIResponse apiResponse = new StandardAPIResponse(
                e.getMessage(),
                httpStatus,
                ZonedDateTime.now(ZoneId.of("Z"))
        );

        return new ResponseEntity(apiResponse, httpStatus);
    }

    /** Get the appropriate Http Status for the AO exception being handled **/
    private HttpStatus getHttpStatusForAOException(
            AccountOpeningServiceException e){

        if (e.getIsDueToBadRequest() || e.getIsDueToIdentityValidationFailure()) {
            return HttpStatus.BAD_REQUEST;

        } else if (e.getIsDueToServiceOutage()){
            return HttpStatus.SERVICE_UNAVAILABLE;

        } else if (e.getIsDueToInvalidRequestID()){
            return HttpStatus.NOT_FOUND;

        } else if (e.getCustomerAlreadyExists() | e.getCustomerInformationNotFound()) {
            return HttpStatus.CONFLICT;

        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /** Handle exceptions thrown by the sanction screening service **/
    @ExceptionHandler(value = {SanctionScreeningException.class})
    public ResponseEntity<SanctionScreeningResultUploadResponse> handleSanctionScreeningException(
            SanctionScreeningException e){

        log.error(e.getMessage());

        HttpStatus httpStatus = this.getHttpStatusForSanctionScreeningException(e);

        final StandardAPIResponse apiResponse;
        apiResponse = new StandardAPIResponse(e.getMessage(), httpStatus, ZonedDateTime.now(ZoneId.of("Z")));

        return new ResponseEntity(apiResponse, httpStatus);
    }

    /** Get the appropriate Http Status for the sanction screening exception being handled **/
    private HttpStatus getHttpStatusForSanctionScreeningException(
            SanctionScreeningException e){

        if (e.getDueToBadResultImage()){
            return  HttpStatus.BAD_REQUEST;
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
