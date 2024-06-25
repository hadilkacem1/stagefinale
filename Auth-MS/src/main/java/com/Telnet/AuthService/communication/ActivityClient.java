package com.Telnet.AuthService.communication;


import com.Telnet.AuthService.model.Activity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "projet-service", url = "${projet-service.url}") // Provide the correct name and URL
public interface ActivityClient {

    @GetMapping("/user/activity/{userId}") // Make sure the endpoint path matches the server
    ResponseEntity<List<Activity>> getActivitiesByUserId(@PathVariable("userId") Integer userId);
}
