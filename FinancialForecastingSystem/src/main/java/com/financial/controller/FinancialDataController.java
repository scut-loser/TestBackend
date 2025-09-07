package com.financial.controller;

import com.financial.entity.FinancialData;
import com.financial.service.FinancialDataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/financial-data")
@CrossOrigin(origins="*")
public class FinancialDataController {
    private final FinancialDataService service;
    public FinancialDataController(FinancialDataService service){ this.service=service; }

    @GetMapping
    public ResponseEntity<Page<FinancialData>> page(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size,
            @RequestParam(required=false) String symbol){
        return ResponseEntity.ok(service.page(symbol, PageRequest.of(page, size)));
    }

    /**
     * 获取金融数据列表（分页）
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<FinancialData>> range(
        @RequestParam String symbol,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime){
        return ResponseEntity.ok(service.range(symbol, startTime, endTime));
    }

    /**
     * 根据时间范围查询数据
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody FinancialData body){
        return ResponseEntity.ok(Map.of("success", true, "data", service.save(body)));
    }

    /**
     * 更新金融数据
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody FinancialData body){
        body.setId(id);
        return ResponseEntity.ok(Map.of("success", true, "data", service.save(body)));
    }

    /**
     * 获取异常数据
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }
}