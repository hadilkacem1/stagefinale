package com.Telnet.volet.Controller;


import com.Telnet.volet.model.Cadran;
import com.Telnet.volet.model.EAxe;
import com.Telnet.volet.model.Volet;
import com.Telnet.volet.repository.CadranRepository;
import com.Telnet.volet.repository.VoletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/volet")
public class VoletController {

    @Autowired
    private VoletRepository voletRepository;

    @Autowired
    private CadranRepository cadranRepository;



    @GetMapping("/count/{axe}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public long countByAxe(@PathVariable String axe) {
        EAxe eAxe = null;
        if (axe.equalsIgnoreCase("interne")) {
            eAxe = EAxe.INTERNE;
        } else if (axe.equalsIgnoreCase("externe")) {
            eAxe = EAxe.EXTERNE;
        } else {
            throw new IllegalArgumentException("Invalid axe: " + axe);
        }

        return voletRepository.countByAxe(eAxe);
    }





    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Volet> getVoletList() {
        return voletRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Volet> getVoletById(@PathVariable(value = "id") Long voletId)
            throws ResourceNotFoundException {
        Volet volet = voletRepository.findById(voletId)
                .orElseThrow(() -> new ResourceNotFoundException("Volet not found ::" + voletId));
        return ResponseEntity.ok().body(volet);
    }

    @GetMapping("/list/{axe}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Volet> getListByAxe(@PathVariable(value = "axe") EAxe axe) {
        return voletRepository.findByAxe(axe);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Volet createVolet(@Valid @RequestBody Volet volet) {
        return voletRepository.save(volet);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Volet> updateVolet(
            @PathVariable(value = "id") Long voletId,
            @Valid @RequestBody Volet voletDetails) throws ResourceNotFoundException {
        Volet volet = voletRepository.findById(voletId)
                .orElseThrow(() -> new ResourceNotFoundException("Volet not found :: " + voletId));

        volet.setName(voletDetails.getName());
        volet.setAxe(voletDetails.getAxe()); // Mettez à jour l'axe du volet

        final Volet updatedVolet = voletRepository.save(volet);
        return ResponseEntity.ok(updatedVolet);
    }

    @GetMapping("/voletNameByCadranName/{cadranName}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String getVoletNameByCadranName(@PathVariable String cadranName) {
        Cadran cadran = cadranRepository.findByName(cadranName);

        if (cadran != null && cadran.getVolet() != null) {
            return cadran.getVolet().getName();
        } else {
            return null; // Ou une valeur par défaut selon vos besoins
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Map<String, Boolean> deleteVolet(
            @PathVariable(value = "id") Long voletId) throws ResourceNotFoundException {
        Volet instructor = voletRepository.findById(voletId)
                .orElseThrow(() -> new ResourceNotFoundException("Volet not found :: " + voletId));

        voletRepository.delete(instructor);
        Map<String, Boolean> response = new HashMap<>();
        response.put("Volet successefully deleted", Boolean.TRUE);
        return response;
    }




}
