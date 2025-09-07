package com.financial.repository;

import com.financial.entity.PredictionResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionResultRepository extends JpaRepository<PredictionResult, Long> {
    List<PredictionResult> findTop50BySymbolOrderByPredictionTimeDesc(String symbol);
}