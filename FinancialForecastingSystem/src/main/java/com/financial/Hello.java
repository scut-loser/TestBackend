package com.financial.controller;   // 包名按你项目来

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Financial Forecasting System is UP! ✅";
    }
}