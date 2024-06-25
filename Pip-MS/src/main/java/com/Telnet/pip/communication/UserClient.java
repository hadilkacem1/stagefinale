package com.Telnet.pip.communication;

import com.Telnet.pip.config.FeignConfig;
import com.Telnet.pip.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Component
@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/users/{email}")
    User getUserByEmail(@PathVariable("email") String email);
}



