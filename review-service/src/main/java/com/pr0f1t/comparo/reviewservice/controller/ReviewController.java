package com.pr0f1t.comparo.reviewservice.controller;

import com.pr0f1t.comparo.reviewservice.dto.ReviewCreateRequest;
import com.pr0f1t.comparo.reviewservice.dto.ReviewUpdateRequest;
import com.pr0f1t.comparo.reviewservice.dto.response.ReviewResponse;
import com.pr0f1t.comparo.reviewservice.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable String productId) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewCreateRequest request,
                                                       JwtAuthenticationToken auth){
        String userId = extractUserId(auth);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(request,userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable UUID id,
                                                       @Valid @RequestBody ReviewUpdateRequest request,
                                                       JwtAuthenticationToken auth){
        String userId = extractUserId(auth);
        return ResponseEntity.ok(reviewService.updateReview(id, request, userId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(@PathVariable UUID id, JwtAuthenticationToken auth){
        String userId = extractUserId(auth);
        reviewService.deleteReview(id, userId);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewResponse>> getPendingReviews() {
        return ResponseEntity.ok(reviewService.getPendingReviews());
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> approveReview(@PathVariable UUID id){
        reviewService.approveReview(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> rejectReview(@PathVariable UUID id) {
        reviewService.rejectReview(id);
        return ResponseEntity.ok().build();
    }

    private String extractUserId(JwtAuthenticationToken auth) {
        return auth.getTokenAttributes().get("sub").toString();
    }

}
