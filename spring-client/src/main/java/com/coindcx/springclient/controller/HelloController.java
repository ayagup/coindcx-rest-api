package com.coindcx.springclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World from CoinDCX Spring Client!";
    }

    @GetMapping("/")
    public String home() {
        return "Welcome to CoinDCX API Client Application";
    }
}
