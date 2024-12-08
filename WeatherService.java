package com.example.demo;


import org.springframework.cache.annotation.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.cache.Cache;


@Service
public class WeatherService {

    private final String API_URL = "https://api.openweathermap.org/data/2.5/weather";
    private final String API_KEY = "a7e09df54610758c16fde2276cfa13b3"; // Replace with your actual API key
    private final String FORECAST_API_URL = "https://api.weatherapi.com/v1/forecast.json";
    private final String API_KEY1 = "38fa1840d1964cfdb0c52409240412"; // Replace with your actual API key

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private int totalRequests = 0; // Simple counter for total requests
    private int cacheHits = 0;     // Simple counter for cache hits
    private int cacheMisses = 0;  
   
    @Cacheable(value = "weatherCache", key = "#city")
	public String fetchCurrentWeather(String city) {
    	totalRequests++;
        logger.info("Received request for current weather for city: {}", city);
          String url = String.format("%s?q=%s&appid=%s", API_URL, city, API_KEY);
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject(url, String.class);
        } catch (HttpClientErrorException e) {
            // Handle 404 or other HTTP client errors
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return "{\"error\": \"City not found. Please check the city name and try again.\"}";
            } else {
                return "{\"error\": \"Failed to fetch weather data. Please try again later.\"}";
            }
        } catch (Exception e) {
            // Handle general errors
            return "{\"error\": \"An unexpected error occurred. Please try again later.\"}";
        }
    }
    
    @Cacheable(value = "forecastCache", key = "#city")
	public String fetchWeatherForecast(String city, int days) {
    	 totalRequests++;
         logger.info("Received request for weather forecast for city: {} for {} days", city, days);
    	String url = String.format("%s?key=%s&q=%s&days=%d", FORECAST_API_URL, API_KEY1, city, days);
        RestTemplate restTemplate = new RestTemplate();

        try {
            String response = restTemplate.getForObject(url, String.class);

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray forecastArray = jsonResponse.getJSONObject("forecast").getJSONArray("forecastday");
            JSONArray summarizedData = new JSONArray();

            for (int i = 0; i < forecastArray.length(); i++) {
                JSONObject dayData = forecastArray.getJSONObject(i);
                JSONObject dailySummary = new JSONObject();
                dailySummary.put("date", dayData.getString("date"));
                dailySummary.put("avgTemp", dayData.getJSONObject("day").getDouble("avgtemp_c"));
                dailySummary.put("maxTemp", dayData.getJSONObject("day").getDouble("maxtemp_c"));
                dailySummary.put("minTemp", dayData.getJSONObject("day").getDouble("mintemp_c"));
                dailySummary.put("condition", dayData.getJSONObject("day").getJSONObject("condition").getString("text"));

                summarizedData.put(dailySummary);
            }

            return summarizedData.toString();

        } catch (Exception e) {
            return "{\"error\": \"Failed to fetch forecast data. Please try again later.\"}";
        }
	}
    
    
    // Monitoring Methods for Cache Hits and Misses
    public void logCacheHit(Cache cache) {
        cacheHits++;
        logger.info("Cache hit: {}", cache.getName());
    }

    public void logCacheMiss(Cache cache) {
        cacheMisses++;
        logger.info("Cache miss: {}", cache.getName());
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public int getCacheHits() {
        return cacheHits;
    }

    public int getCacheMisses() {
        return cacheMisses;
    }
   
    
}
