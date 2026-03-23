package com.pr0f1t.comparo.catalogservice.bootstrap;

import com.pr0f1t.comparo.catalogservice.dto.CategoryRequest;
import com.pr0f1t.comparo.catalogservice.dto.ProductRequest;
import com.pr0f1t.comparo.catalogservice.repository.category.CategoryRepository;
import com.pr0f1t.comparo.catalogservice.repository.product.ProductRepository;
import com.pr0f1t.comparo.catalogservice.service.category.CategoryService;
import com.pr0f1t.comparo.catalogservice.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductService productService;

    @Override
    public void run(String... args){

        if (categoryRepository.count() == 0 && productRepository.count() == 0) {
            log.info("Database is empty. Starting English data seeding (20 products)...");
            seedData();
        } else {
            log.info("Database already contains data. Seeding skipped.");
        }

    }

    private void seedData(){

        var laptopsCat = categoryService.createCategory(new CategoryRequest("Laptops", null));
        var phonesCat = categoryService.createCategory(new CategoryRequest("Smartphones", null));

        List<SeedItem> items = List.of(

                new SeedItem("iPhone 15 Pro Max", "Apple flagship with titanium body.", phonesCat.getId(), "1199.00", Map.of("RAM", "8GB", "Storage", "256GB", "Camera", "48MP", "OS", "iOS"), "iphone-15-pro-max.jpg"),
                new SeedItem("iPhone 14", "Reliable and fast Apple smartphone.", phonesCat.getId(), "799.00", Map.of("RAM", "6GB", "Storage", "128GB", "Camera", "12MP", "OS", "iOS"), "iphone-14.jpg"),
                new SeedItem("Samsung Galaxy S24 Ultra", "Premium Android with S Pen.", phonesCat.getId(), "1299.00", Map.of("RAM", "12GB", "Storage", "512GB", "Camera", "200MP", "OS", "Android"), "s24-ultra.jpg"),
                new SeedItem("Samsung Galaxy A54", "Mid-range champion.", phonesCat.getId(), "449.00", Map.of("RAM", "8GB", "Storage", "128GB", "Camera", "50MP", "OS", "Android"), "galaxy-a54.jpg"),
                new SeedItem("Google Pixel 8 Pro", "Best AI camera phone.", phonesCat.getId(), "999.00", Map.of("RAM", "12GB", "Storage", "256GB", "Camera", "50MP", "OS", "Android"), "pixel-8-pro.jpg"),
                new SeedItem("Google Pixel 7a", "Budget-friendly camera king.", phonesCat.getId(), "499.00", Map.of("RAM", "8GB", "Storage", "128GB", "Camera", "64MP", "OS", "Android"), "pixel-7a.jpg"),
                new SeedItem("OnePlus 12", "Hasselblad camera and hyper-fast charging.", phonesCat.getId(), "799.00", Map.of("RAM", "16GB", "Storage", "512GB", "Camera", "50MP", "OS", "Android"), "oneplus-12.jpg"),
                new SeedItem("Xiaomi 14 Pro", "Leica optics and Snapdragon 8 Gen 3.", phonesCat.getId(), "899.00", Map.of("RAM", "12GB", "Storage", "256GB", "Camera", "50MP", "OS", "Android"), "xiaomi-14-pro.jpg"),
                new SeedItem("Sony Xperia 1 V", "For photography professionals.", phonesCat.getId(), "1399.00", Map.of("RAM", "12GB", "Storage", "256GB", "Camera", "48MP", "OS", "Android"), "xperia-1-v.jpg"),
                new SeedItem("Asus ROG Phone 8", "Ultimate gaming smartphone.", phonesCat.getId(), "1099.00", Map.of("RAM", "16GB", "Storage", "512GB", "Camera", "50MP", "OS", "Android"), "rog-phone-8.jpg"),


                new SeedItem("MacBook Pro 16", "M3 Max chip, for heavy professional workflows.", laptopsCat.getId(), "3499.00", Map.of("RAM", "36GB", "Storage", "1TB SSD", "CPU", "M3 Max", "OS", "macOS"), "macbook-pro-16.jpg"),
                new SeedItem("MacBook Air 15", "Lightweight with powerful M3 chip.", laptopsCat.getId(), "1299.00", Map.of("RAM", "16GB", "Storage", "512GB SSD", "CPU", "M3", "OS", "macOS"), "macbook-air-15.jpg"),
                new SeedItem("Dell XPS 15", "Premium Windows ultrabook with OLED.", laptopsCat.getId(), "1899.00", Map.of("RAM", "32GB", "Storage", "1TB SSD", "CPU", "Intel i9", "OS", "Windows 11"), "dell-xps-15.jpg"),
                new SeedItem("Lenovo ThinkPad X1 Carbon", "The ultimate business laptop.", laptopsCat.getId(), "1699.00", Map.of("RAM", "16GB", "Storage", "512GB SSD", "CPU", "Intel i7", "OS", "Windows 11"), "thinkpad-x1.jpg"),
                new SeedItem("Asus ROG Zephyrus G14", "Compact and incredibly powerful gaming laptop.", laptopsCat.getId(), "1599.00", Map.of("RAM", "16GB", "Storage", "1TB SSD", "GPU", "RTX 4060", "OS", "Windows 11"), "zephyrus-g14.jpg"),
                new SeedItem("HP Spectre x360", "Versatile 2-in-1 premium laptop.", laptopsCat.getId(), "1499.00", Map.of("RAM", "16GB", "Storage", "1TB SSD", "CPU", "Intel i7", "OS", "Windows 11"), "hp-spectre.jpg"),
                new SeedItem("Acer Predator Helios 300", "High refresh rate gaming machine.", laptopsCat.getId(), "1299.00", Map.of("RAM", "16GB", "Storage", "1TB SSD", "GPU", "RTX 4070", "OS", "Windows 11"), "predator-helios.jpg"),
                new SeedItem("Razer Blade 16", "Sleek and premium gaming powerhouse.", laptopsCat.getId(), "2699.00", Map.of("RAM", "32GB", "Storage", "1TB SSD", "GPU", "RTX 4080", "OS", "Windows 11"), "razer-blade-16.jpg"),
                new SeedItem("Microsoft Surface Laptop 6", "Clean design and great touch screen.", laptopsCat.getId(), "1199.00", Map.of("RAM", "16GB", "Storage", "512GB SSD", "CPU", "Intel Core Ultra", "OS", "Windows 11"), "surface-laptop-6.jpg"),
                new SeedItem("MSI Stealth 16", "Thin and light studio laptop.", laptopsCat.getId(), "1799.00", Map.of("RAM", "32GB", "Storage", "1TB SSD", "GPU", "RTX 4070", "OS", "Windows 11"), "msi-stealth.jpg")
        );

        for (SeedItem item : items) {
            ProductRequest request = new ProductRequest(item.name, item.desc, item.categoryId, new BigDecimal(item.price), item.specs, List.of());
            List<MultipartFile> imagesToUpload = new ArrayList<>();

            Resource imageResource = new ClassPathResource("demo-images/" + item.imageFile);
            if (imageResource.exists()) {
                imagesToUpload.add(new ResourceMultipartFile(imageResource));
            } else {
                log.warn("Image {} not found in resources/demo-images. Product {} will be created without an image.", item.imageFile, item.name);
            }

            productService.createProduct(request, imagesToUpload);
        }

        log.info("Successfully seeded 20 products. Events published to Kafka.");


    }

    private record SeedItem(String name, String desc, String categoryId, String price, Map<String, String> specs,
                            String imageFile) {}


    private record ResourceMultipartFile(Resource resource) implements MultipartFile {
        @Override
        public String getName() {
            return resource.getFilename();
        }

        @Override
        public String getOriginalFilename() {
            return resource.getFilename();
        }

        @Override
        public String getContentType() {
                try {
                    return Files.probeContentType(resource.getFile().toPath());
                } catch (IOException e) {
                    return "image/jpeg";
                }
            }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public long getSize() {
                try {
                    return resource.contentLength();
                } catch (IOException e) {
                    return 0;
                }
            }

        @Override
        public byte[] getBytes() throws IOException {
            return FileCopyUtils.copyToByteArray(resource.getInputStream());
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return resource.getInputStream();
        }

        @Override
        public void transferTo(File dest) throws IllegalStateException {
            throw new UnsupportedOperationException();
        }
        }

}
