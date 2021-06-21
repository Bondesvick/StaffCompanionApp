package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.mappers;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.DTOs.APIResponses.CustomerInfo;
import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.entities.CustomerInformationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerInfoMapper {

    CustomerInfoMapper MAPPER = Mappers.getMapper(CustomerInfoMapper.class);

    CustomerInfo getCustomerInfo(CustomerInformationEntity customerInformationEntity);
}
