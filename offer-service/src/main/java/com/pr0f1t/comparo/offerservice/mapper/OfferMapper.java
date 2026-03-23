package com.pr0f1t.comparo.offerservice.mapper;

import com.pr0f1t.comparo.offerservice.dto.CreateOfferRequest;
import com.pr0f1t.comparo.offerservice.dto.OfferResponse;
import com.pr0f1t.comparo.offerservice.entity.Offer;
import com.pr0f1t.comparo.offerservice.entity.enums.AvailabilityStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(source = "availabilityStatus", target = "availabilityStatus", qualifiedByName = "stringToEnum")
    Offer toEntity(CreateOfferRequest request);

    OfferResponse toResponse(Offer offer);

    @Named("stringToEnum")
    default AvailabilityStatus stringToEnum(String status) {
        if (status == null || status.isBlank()) {
            return AvailabilityStatus.OUT_OF_STOCK;
        }
        try {
            return AvailabilityStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return AvailabilityStatus.OUT_OF_STOCK;
        }
    }
}