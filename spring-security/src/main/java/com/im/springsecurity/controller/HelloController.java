package com.im.springsecurity.controller;

import org.springframework.web.bind.annotation.*;

/**
 * @author 86199
 */
@RestController
@RequestMapping("/hello")
public class HelloController {

    @GetMapping
    public String sayHello() {
        return "Hello World";
    }

    @PostMapping
    public String sayHello(@RequestParam String name) {
        return "Hello " + name;
    }

    @PostMapping("/auth")
    public String auth(){
        return "You are authenticated";
    }
}
