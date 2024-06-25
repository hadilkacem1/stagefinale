package com.Telnet.projet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableDiscoveryClient
public class ProjetApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetApplication.class, args);
	}


	// Supprimez cette méthode pour enlever le bean
    /*
    @Bean
    ApplicationRunner init() {
        return (args -> {
            repository.save(new ProjetModel("Sarra","ab",466,""));
            repository.save(new ProjetModel("Mariem","Ch",999,"anglais"));
            repository.findAll().forEach(System.out::println);
        });
    }
    */
}
