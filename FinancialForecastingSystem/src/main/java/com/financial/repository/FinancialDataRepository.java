package com.financial.repository;

import com.financial.entity.FinancialData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FinancialDataRepository extends JpaRepository<FinancialData, Long> {
    Page<FinancialData> findBySymbol(String symbol, Pageable pageable);
    List<FinancialData> findBySymbolAndDateTimeBetweenOrderByDateTimeAsc(String symbol, LocalDateTime start, LocalDateTime end);
    FinancialData findTop1BySymbolOrderByDateTimeDesc(String symbol);
}