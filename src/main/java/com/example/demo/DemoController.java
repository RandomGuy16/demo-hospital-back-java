package com.example.demo;

import com.example.demo.dto.DemoResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @GetMapping("/api")
    public DemoResponseBody Hello() {
        return new DemoResponseBody("Hello World!");
    }
}
