package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.mappers;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIResponses.AccountOpeningRequestData;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.AccountOpeningRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AccountOpeningRequestMapper {

    AccountOpeningRequestMapper MAPPER = Mappers.getMapper(AccountOpeningRequestMapper.class);

    AccountOpeningRequestData getAORequestData(AccountOpeningRequestEntity AORequestEntity);
    }
