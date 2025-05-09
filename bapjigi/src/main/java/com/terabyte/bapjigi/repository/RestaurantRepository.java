package com.terabyte.bapjigi.repository;

import com.terabyte.bapjigi.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByCategory(String category);
    Page<Restaurant> findByNameContainingOrAddressContainingOrDescriptionContaining(String name, String address, String description, Pageable pageable);
    List<Restaurant> findByAveragePriceLessThanEqual(Integer averagePrice);
} 