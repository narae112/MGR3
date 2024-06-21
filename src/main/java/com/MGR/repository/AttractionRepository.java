package com.MGR.repository;

import com.MGR.entity.Attraction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttractionRepository extends JpaRepository<Attraction, Long> {


    Optional<Object> findByName(String name);
}
