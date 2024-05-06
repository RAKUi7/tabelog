package com.example.tabelog_nagoyameshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FooterController {
	@GetMapping("/terms")
    public String showTerms() {
        return "admin/footer/terms";
    }

    @GetMapping("/privacy")
    public String showPrivacyPolicy() {
        return "admin/footer/privacy"; 
    }

    @GetMapping("/contact")
    public String showContactInfo() {
        return "admin/footer/contact"; 
    }
}
