package com.Telnet.projet.communication;



import com.Telnet.projet.config.FeignConfig;
import com.Telnet.projet.models.Activity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Component
@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface ActivityClient {

    @GetMapping("/user/activity/{userId}")
    ResponseEntity<List<Activity>> getActivitiesByUserId(@PathVariable("userId") Integer userId);
}
