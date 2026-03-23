package com.pr0f1t.comparo.offerservice.service;

import com.pr0f1t.comparo.offerservice.dto.CreateOfferRequest;
import com.pr0f1t.comparo.offerservice.dto.OfferIngestedEvent;
import com.pr0f1t.comparo.offerservice.dto.OfferPriceAggregateEvent;
import com.pr0f1t.comparo.offerservice.dto.OfferResponse;
import com.pr0f1t.comparo.offerservice.entity.Offer;
import com.pr0f1t.comparo.offerservice.entity.enums.AvailabilityStatus;
import com.pr0f1t.comparo.offerservice.exception.OfferNotFoundException;
import com.pr0f1t.comparo.offerservice.mapper.OfferMapper;
import com.pr0f1t.comparo.offerservice.kafka.producer.OfferEventProducer;
import com.pr0f1t.comparo.offerservice.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final OfferEventProducer offerEventProducer;
    private final OfferMapper offerMapper;

    @Override
    @Transactional
    public OfferResponse createOffer(CreateOfferRequest request) {

        Offer offer = Offer.builder()
                .productId(request.productId())
                .shopId(request.shopId())
                .price(request.price())
                .currency(request.currency())
                .url(request.url())
                .availabilityStatus(parseStatus(request.availabilityStatus()))
                .build();

        Offer savedOffer = offerRepository.save(offer);

        publishPriceAggregate(savedOffer.getProductId());

        return offerMapper.toResponse(savedOffer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferResponse> getOffersByProductId(String productId) {
        return offerRepository.findByProductId(productId).stream()
                .map(offerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OfferResponse getOfferById(UUID id) {
        log.info("Fetching offer by ID: {}", id);

        return offerRepository.findById(id)
                .map(offerMapper::toResponse)
                .orElseThrow(() -> new OfferNotFoundException("Offer with ID " + id + " not found"));
    }

    @Override
    @Transactional
    public void deleteOffer(UUID id) {
        log.info("Attempting to delete offer with ID: {}", id);

        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new OfferNotFoundException("Cannot delete: Offer with ID " + id + " not found"));

        String prodId = offer.getProductId();
        offerRepository.deleteById(id);
        log.info("Offer with ID {} successfully deleted", id);

        publishPriceAggregate(prodId);
    }


    @Override
    @Transactional
    public void processIngestedOffer(OfferIngestedEvent event) {

        Optional<Offer> existingOfferOpt = offerRepository.findByProductIdAndShopId(event.productId(), event.shopId());

        AvailabilityStatus newStatus = offerMapper.stringToEnum(event.rawAvailabilityStatus());

        if (existingOfferOpt.isPresent()) {
            Offer existingOffer = existingOfferOpt.get();
            BigDecimal oldPrice = existingOffer.getPrice();

            boolean priceChanged = oldPrice.compareTo(event.price()) != 0;
            boolean statusChanged = existingOffer.getAvailabilityStatus() != newStatus;

            if (priceChanged || statusChanged) {
                existingOffer.setPrice(event.price());
                existingOffer.setCurrency(event.currency());
                existingOffer.setAvailabilityStatus(newStatus);
                existingOffer.setUrl(event.url());

                offerRepository.save(existingOffer);

                if (priceChanged) {
                    publishPriceAggregate(existingOffer.getProductId());
                }
            }

        } else {

            Offer newOffer = Offer.builder()
                    .productId(event.productId())
                    .shopId(event.shopId())
                    .price(event.price())
                    .currency(event.currency())
                    .url(event.url())
                    .availabilityStatus(newStatus)
                    .build();

            offerRepository.save(newOffer);

            publishPriceAggregate(event.productId());
        }

    }

    private void publishPriceAggregate(String productId) {
        BigDecimal minPrice = offerRepository.findMinPriceByProductId(productId);
        BigDecimal maxPrice = offerRepository.findMaxPriceByProductId(productId);

        if (minPrice != null && maxPrice != null) {
            offerEventProducer.sendPriceAggregateEvent(OfferPriceAggregateEvent.builder()
                    .productId(productId)
                    .minPrice(minPrice)
                    .maxPrice(maxPrice)
                    .build());
        }
    }

    private AvailabilityStatus parseStatus(String rawStatus) {
        try {
            return AvailabilityStatus.valueOf(rawStatus.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return AvailabilityStatus.OUT_OF_STOCK;
        }
    }
}
