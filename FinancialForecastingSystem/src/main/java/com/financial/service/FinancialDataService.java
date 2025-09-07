package com.financial.service;

import com.financial.entity.FinancialData;
import com.financial.repository.FinancialDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinancialDataService {
    private final FinancialDataRepository repo;
    public FinancialDataService(FinancialDataRepository repo){ this.repo=repo; }

    public Page<FinancialData> page(String symbol, Pageable pageable){
        if (symbol==null || symbol.isEmpty()) return repo.findAll(pageable);
        return repo.findBySymbol(symbol, pageable);
    }

    public FinancialData byId(Long id){ return repo.findById(id).orElse(null); }

    public FinancialData latest(String symbol){ return repo.findTop1BySymbolOrderByDateTimeDesc(symbol); }

    public List<FinancialData> range(String symbol, LocalDateTime start, LocalDateTime end){
        return repo.findBySymbolAndDateTimeBetweenOrderByDateTimeAsc(symbol, start, end);
    }

    public FinancialData save(FinancialData fd){ return repo.save(fd); }
    public void delete(Long id){ repo.deleteById(id); }
}