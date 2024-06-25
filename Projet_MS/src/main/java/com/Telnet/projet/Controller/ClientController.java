package com.Telnet.projet.Controller;



import com.Telnet.projet.models.Client;
import com.Telnet.projet.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/clients")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    // Récupérer tous les clients
    @GetMapping("/allclients")
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    // Récupérer un client par son ID
    @GetMapping("getby/{id}")
    public Client getClientById(@PathVariable Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    // Créer un nouveau client
    @PostMapping("/addclient")
    public Client createClient(@RequestBody Client client) {
        return clientRepository.save(client);
    }

    // Supprimer un client par son ID
    @DeleteMapping("/delete/{id}")
    public void deleteClients(@PathVariable Long id) {
        clientRepository.deleteById(id);
    }

    // Mettre à jour un client existant
    @PutMapping("/{id}")
    public Client updateClient(@PathVariable Long id, @RequestBody Client updatedClient) {
        return clientRepository.findById(id)
                .map(client -> {
                    client.setName(updatedClient.getName());
                    client.setPhone(updatedClient.getPhone());
                    client.setActive(updatedClient.isActive());
                    return clientRepository.save(client);
                })
                .orElse(null);
    }



    // Supprimer un client par son ID
    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Long id) {
        clientRepository.deleteById(id);
    }
}
