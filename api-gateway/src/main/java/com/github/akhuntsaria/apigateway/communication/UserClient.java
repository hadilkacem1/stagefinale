package com.github.akhuntsaria.apigateway.communication;



import com.github.akhuntsaria.apigateway.config.FeignConfig;
import com.github.akhuntsaria.apigateway.model.User;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


//@FeignClient(name = "AUTH-SERVICE", url = "http://auth-service:8805")
@Component
@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/users/{email}")
    User getUserByEmail(@PathVariable("email") String email);
}



