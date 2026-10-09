package com.basket.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vendor")
public class VendorController {

    @GetMapping("/test")
    public String vendorTest(){
        return "vendor access successful";
    }
}
