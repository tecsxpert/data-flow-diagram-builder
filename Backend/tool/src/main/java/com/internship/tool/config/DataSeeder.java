package com.internship.tool.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.internship.tool.entity.DfdRecord;
import com.internship.tool.repository.DfdRecordRepository;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private DfdRecordRepository repository;

    @Override
    public void run(String... args) throws Exception {

        if (repository.count() > 0) return; // avoid duplicate seeding

        List<DfdRecord> records = List.of(
                create("Login System", "User login flow", "ACTIVE"),
                create("Payment Gateway", "Payment processing", "COMPLETED"),
                create("Cart System", "Add to cart", "ACTIVE"),
                create("Order System", "Order placement", "COMPLETED"),
                create("Search Feature", "Search products", "ACTIVE"),
                create("Notification System", "Email alerts", "ACTIVE"),
                create("Admin Panel", "Admin dashboard", "COMPLETED"),
                create("Review System", "User reviews", "ACTIVE"),
                create("Shipping Module", "Delivery system", "COMPLETED"),
                create("Profile Page", "User profile", "ACTIVE"),
                create("Auth Module", "JWT login", "COMPLETED"),
                create("Analytics", "User analytics", "ACTIVE"),
                create("Recommendation Engine", "AI suggestions", "ACTIVE"),
                create("Security Layer", "Data protection", "COMPLETED"),
                create("Audit Logging", "Track actions", "ACTIVE")
        );

        repository.saveAll(records);
    }

    private DfdRecord create(String title, String desc, String status) {
        DfdRecord r = new DfdRecord();
        r.setTitle(title);
        r.setDescription(desc);
        r.setStatus(status);
        return r;
    }
}