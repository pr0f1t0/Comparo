package com.pr0f1t.comparo.offerservice.repository;

import com.pr0f1t.comparo.offerservice.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {

    List<Offer> findByProductId(String productId);

    Optional<Offer> findByProductIdAndShopId(String productId, UUID shopId);

    Optional<Offer> findFirstByProductIdOrderByPriceAsc(String productId);

    boolean existsByShopId(UUID shopId);

    @Query("SELECT MIN(o.price) FROM Offer o WHERE o.productId = :productId")
    BigDecimal findMinPriceByProductId(@Param("productId") String productId);

    @Query("SELECT MAX(o.price) FROM Offer o WHERE o.productId = :productId")
    BigDecimal findMaxPriceByProductId(@Param("productId") String productId);
}