package com.pr0f1t.comparo.offerservice.bootstrap;

import com.pr0f1t.comparo.offerservice.dto.CreateOfferRequest;
import com.pr0f1t.comparo.offerservice.repository.OfferRepository;
import com.pr0f1t.comparo.offerservice.service.OfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
@DependsOn("entityManagerFactory")
public class DataSeeder implements CommandLineRunner {

    private final OfferRepository offerRepository;
    private final OfferService offerService;
    private final String catalogServiceUrl;

    private static final List<ShopInfo> SHOPS = List.of(
            new ShopInfo("TechStore", "https://techstore.example.com/product/"),
            new ShopInfo("MegaElectronics", "https://megaelectronics.example.com/item/"),
            new ShopInfo("BudgetGadgets", "https://budgetgadgets.example.com/p/")
    );

    private static final Random RANDOM = new Random(42);

    public DataSeeder(OfferRepository offerRepository,
                      OfferService offerService,
                      @Value("${app.catalog-service.url:http://localhost:8003}") String catalogServiceUrl) {
        this.offerRepository = offerRepository;
        this.offerService = offerService;
        this.catalogServiceUrl = catalogServiceUrl;
    }

    @Override
    public void run(String... args) {
        try {
            if (offerRepository.count() > 0) {
                log.info("Offers already exist. Seeding skipped.");
                return;
            }
        } catch (Exception e) {
            log.error("Cannot access offers table. Liquibase may have failed: {}", e.getMessage());
            return;
        }

        log.info("No offers found. Fetching products from catalog service at {}", catalogServiceUrl);

        List<Map<String, Object>> products;
        try {
            RestClient restClient = RestClient.create();
            products = restClient.get()
                    .uri(catalogServiceUrl + "/api/v1/catalog/products")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            log.warn("Could not fetch products from catalog service: {}. Offer seeding skipped.", e.getMessage());
            return;
        }

        if (products == null || products.isEmpty()) {
            log.warn("No products returned from catalog service. Offer seeding skipped.");
            return;
        }

        log.info("Fetched {} products. Creating offers...", products.size());

        int totalOffers = 0;
        for (Map<String, Object> product : products) {
            String productId = (String) product.get("id");
            Object basePriceObj = product.get("basePrice");
            if (productId == null || basePriceObj == null) {
                continue;
            }

            BigDecimal basePrice = new BigDecimal(basePriceObj.toString());
            int offerCount = 2 + RANDOM.nextInt(2); // 2 or 3 offers per product

            for (int i = 0; i < offerCount; i++) {
                ShopInfo shop = SHOPS.get(i % SHOPS.size());
                // Vary price: -10% to +15% from base price
                double factor = 0.90 + (RANDOM.nextDouble() * 0.25);
                BigDecimal price = basePrice.multiply(BigDecimal.valueOf(factor))
                        .setScale(2, RoundingMode.HALF_UP);

                CreateOfferRequest request = CreateOfferRequest.builder()
                        .productId(productId)
                        .shopId(UUID.nameUUIDFromBytes((shop.name + "-" + productId).getBytes()))
                        .price(price)
                        .currency("USD")
                        .url(shop.urlPrefix + productId)
                        .availabilityStatus("IN_STOCK")
                        .build();

                try {
                    offerService.createOffer(request);
                    totalOffers++;
                } catch (Exception e) {
                    log.warn("Failed to create offer for product {}: {}", productId, e.getMessage());
                }
            }
        }

        log.info("Successfully seeded {} offers for {} products. Price aggregate events published to Kafka.", totalOffers, products.size());
    }

    private record ShopInfo(String name, String urlPrefix) {}
}
