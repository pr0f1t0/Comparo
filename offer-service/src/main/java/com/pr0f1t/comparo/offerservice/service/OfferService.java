package com.pr0f1t.comparo.offerservice.service;

import com.pr0f1t.comparo.offerservice.dto.CreateOfferRequest;
import com.pr0f1t.comparo.offerservice.dto.OfferIngestedEvent;
import com.pr0f1t.comparo.offerservice.dto.OfferResponse;

import java.util.List;
import java.util.UUID;

public interface OfferService {
    OfferResponse createOffer(CreateOfferRequest request);
    List<OfferResponse> getOffersByProductId(String productId);
    void processIngestedOffer(OfferIngestedEvent event);
    OfferResponse getOfferById(UUID id);
    void deleteOffer(UUID id);
}
