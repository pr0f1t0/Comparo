package com.pr0f1t.comparo.offerservice.controller;

import com.pr0f1t.comparo.offerservice.dto.CreateOfferRequest;
import com.pr0f1t.comparo.offerservice.dto.OfferResponse;
import com.pr0f1t.comparo.offerservice.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;


    @GetMapping("/product/{productId}")
    public ResponseEntity<List<OfferResponse>> getOffersByProduct(@PathVariable String productId) {
        log.info("Request to fetch offers for product ID: {}", productId);
        List<OfferResponse> offers = offerService.getOffersByProductId(productId);
        return ResponseEntity.ok(offers);
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OfferResponse> createOffer(@Valid @RequestBody CreateOfferRequest request) {
        log.info("Admin request to create new offer for product: {}", request.productId());
        OfferResponse response = offerService.createOffer(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<OfferResponse> getOfferById(@PathVariable UUID id) {
        log.info("Request to fetch offer details for ID: {}", id);
        OfferResponse offer = offerService.getOfferById(id);
        return ResponseEntity.ok(offer);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOffer(@PathVariable UUID id) {
        log.info("Admin request to delete offer ID: {}", id);
        offerService.deleteOffer(id);
    }
}
