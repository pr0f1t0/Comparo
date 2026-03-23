package com.pr0f1t.comparo.reviewservice.mapper;

import com.pr0f1t.comparo.reviewservice.dto.ReviewCreateRequest;
import com.pr0f1t.comparo.reviewservice.dto.response.ReviewResponse;
import com.pr0f1t.comparo.reviewservice.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ReviewMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "moderationStatus", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(ReviewCreateRequest request);

    @Mapping(source = "moderationStatus", target = "status")
    ReviewResponse toResponse(Review review);
}
