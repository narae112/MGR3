package com.MGR.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class LocationController {

    @GetMapping("/location")
    public String showLocation(){
        return "/location/main";
    }
    @GetMapping("/location/busan")
    public String showBusan(){
        return "/location/busan";
    }
    @GetMapping("/location/seoul")
    public String showSeoul(){
        return "/location/seoul";
    }
}
