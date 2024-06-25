package com.Telnet.projet.repository;



import com.Telnet.projet.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
