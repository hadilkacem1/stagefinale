package com.Telnet.volet.Controller;

import com.Telnet.volet.model.Cadran;
import com.Telnet.volet.model.EType;
import com.Telnet.volet.model.Volet;
import com.Telnet.volet.repository.CadranRepository;
import com.Telnet.volet.repository.VoletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/cadran")
public class CadranController {

    @Autowired
    private CadranRepository cadranRepository;

    @Autowired
    private VoletRepository voletRepository;

    // ------------------------------- AJOUTER CADRAN AVEC VOLET ID ---------------------------------------------------
    @PostMapping("/volet/{volet_id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> createCadran(@PathVariable(value = "volet_id") Long voletId, @RequestBody Cadran cadran) {
        try {
            Volet volet = voletRepository.findById(voletId)
                    .orElseThrow(() -> new ResourceNotFoundException("Volet not found with id: " + voletId));

            Cadran newCadran = new Cadran(cadran.getName(), cadran.getType(), volet);
            Cadran createdCadran = cadranRepository.save(newCadran);

            return ResponseEntity.ok().body(createdCadran);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("Error creating Cadran: " + ex.getMessage());
        }
    }
    // ---------------------------------------------------------------------------------------------------

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Cadran> getCadranList() {
        return cadranRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Cadran> getCadranById(@PathVariable(value = "id") Long cadranId)
            throws ResourceNotFoundException {
        Cadran cadran = cadranRepository.findById(cadranId)
                .orElseThrow(() -> new ResourceNotFoundException("Cadran not found ::" + cadranId));
        return ResponseEntity.ok().body(cadran);
    }

    // Get Cadrans list with axe name passed as a parameter
    @GetMapping("/list/{axe}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Cadran> getCadranListByAxeVolet(@PathVariable(value = "axe") String axe) {
        return cadranRepository.findByAxe(axe);
    }

    @GetMapping("/list/strength")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Cadran> getCadranListByTypeS() {
        return cadranRepository.findStrength();
    }

    @GetMapping("/list/weakness")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Cadran> getCadranListByTypeW() {
        return cadranRepository.findWeakness();
    }

    @GetMapping("/list/opportunity")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Cadran> getCadranListByTypeO() {
        return cadranRepository.findOpportunity();
    }

    @GetMapping("/list/threat")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Cadran> getCadranListByTypeT() {
        return cadranRepository.findThreat();
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Cadran createCadran(@Valid @RequestBody Cadran cadran) {
        return cadranRepository.save(cadran);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Cadran> updateCadran(
            @PathVariable(value = "id") Long cadranId,
            @Valid @RequestBody Cadran cadranDetails) throws ResourceNotFoundException {
        Cadran cadran = cadranRepository.findById(cadranId)
                .orElseThrow(() -> new ResourceNotFoundException("Cadran not found :: " + cadranId));
        cadran.setName(cadranDetails.getName());
        final Cadran updatedCadran = cadranRepository.save(cadran);
        return ResponseEntity.ok(updatedCadran);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Map<String, Boolean> deleteCadran(
            @PathVariable(value = "id") Long cadranId) throws ResourceNotFoundException {
        Cadran cadran = cadranRepository.findById(cadranId)
                .orElseThrow(() -> new ResourceNotFoundException("Cadran not found :: " + cadranId));

        cadranRepository.delete(cadran);
        Map<String, Boolean> response = new HashMap<>();
        response.put("Cadran successefully deleted", Boolean.TRUE);
        return response;
    }
    // Endpoint pour obtenir les actions prioritaires
    @GetMapping("/actions-prioritaires")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, List<Cadran>>> getPrioritizedActions() {
        List<Cadran> forces = cadranRepository.findStrength();
        List<Cadran> faiblesses = cadranRepository.findWeakness();
        List<Cadran> opportunites = cadranRepository.findOpportunity();
        List<Cadran> menaces = cadranRepository.findThreat();

        // Actions prioritaires : forces avec opportunités et faiblesses avec menaces
        List<Cadran> actionsPrioritaires = new ArrayList<>();
        actionsPrioritaires.addAll(forces);
        actionsPrioritaires.addAll(opportunites);
        actionsPrioritaires.addAll(faiblesses);
        actionsPrioritaires.addAll(menaces);

        // Créer une carte pour stocker les actions prioritaires par catégorie
        Map<String, List<Cadran>> prioritizedActions = new HashMap<>();
        prioritizedActions.put("forces_opportunites", forces);
        prioritizedActions.put("faiblesses_menaces", faiblesses);

        return ResponseEntity.ok(prioritizedActions);
    }
    @GetMapping("/statistiques")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Map<String, Long>> getStatisticsByType() {
        // Récupérer le nombre d'occurrences de chaque type depuis la base de données
        Long forceCount = cadranRepository.countByType(EType.STRENGTH);
        Long faiblesseCount = cadranRepository.countByType(EType.WEAKNESS);
        Long opportuniteCount = cadranRepository.countByType(EType.OPPORTUNITY);
        Long menaceCount = cadranRepository.countByType(EType.THREAT);

        Map<String, Long> statistics = new HashMap<>();
        statistics.put("forceCount", forceCount);
        statistics.put("faiblesseCount", faiblesseCount);
        statistics.put("opportuniteCount", opportuniteCount);
        statistics.put("menaceCount", menaceCount);

        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/volet/{voletId}/cadrans")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Cadran>> getCadransByVoletId(@PathVariable(value = "voletId") Long voletId) {
        List<Cadran> cadrans = cadranRepository.findByVoletId(voletId);
        return ResponseEntity.ok(cadrans);
    }

}
