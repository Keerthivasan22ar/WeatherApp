package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;
    
    @GetMapping("/weather")
    public String getWeather(@RequestParam String city) {
        return weatherService.fetchCurrentWeather(city);
    }
    @GetMapping("/forecast")
    public String getWeatherForecast(@RequestParam String city, @RequestParam int days) {
        return weatherService.fetchWeatherForecast(city, days);
    }
}
