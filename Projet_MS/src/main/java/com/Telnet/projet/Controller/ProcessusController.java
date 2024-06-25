package com.Telnet.projet.Controller;


import com.Telnet.projet.models.Kpi;
import com.Telnet.projet.models.Processus;
import com.Telnet.projet.repository.KpiHistoryRepository;
import com.Telnet.projet.repository.ProcessusRepository;
import com.Telnet.projet.repository.kpiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/processus")

public class ProcessusController {

 @Autowired
 ProcessusRepository processusRepository;

@Autowired
    kpiRepository kpiRepository;

@Autowired
KpiHistoryRepository kpiHistoryRepository;

    @GetMapping("/list")
    public List<Processus> getProcessusList() {
        return processusRepository.findAll();
    }





    @PostMapping("/add")
    public Processus createProcessus(@Valid @RequestBody Processus processus) {
        return processusRepository.save(processus);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Processus> updateProcessus(
            @PathVariable(value = "id") Long processusId,
            @Valid @RequestBody Processus processusDetails) throws ResourceNotFoundException {
        Processus processus = processusRepository.findById(processusId)
                .orElseThrow(() -> new ResourceNotFoundException("Processus not found :: " + processusId));

        // Mettre à jour les détails du processus avec ceux fournis dans le corps de la requête
        processus.setName(processusDetails.getName());
        processus.setDescription(processusDetails.getDescription());
        processus.setKpis(processusDetails.getKpis()); // Mettre à jour les KPI associés

        final Processus updatedProcessus = processusRepository.save(processus);
        return ResponseEntity.ok(updatedProcessus);
    }





   /* @PostMapping("/addp")
    public Processus createProcessuss(@Valid @RequestBody Processus processus) {
        // Vérifier si des KPI ont été fournis
        Set<Kpi> kpis = processus.getKpis();
        if (kpis != null && !kpis.isEmpty()) {
            // Créer une nouvelle liste pour les KPI associés au processus
            Set<Kpi> associatedKpis = new HashSet<>();

            // Boucler sur chaque KPI fourni et associer au processus
            for (Kpi kpi : kpis) {
                // Vérifier si le KPI est null avant d'accéder à sa méthode getId()
                if (kpi != null) {
                    // Assurer que le KPI existe en base de données en récupérant son ID
                    Kpi existingKpi = kpiRepository.findById(kpi.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("KPI not found with ID: " + kpi.getId()));
                    // Ajouter le KPI à la liste des KPI associés au processus
                    associatedKpis.add(existingKpi);
                }
            }

            // Associer les KPI au processus
            processus.setKpis(associatedKpis);
        }

        // Enregistrer le processus avec les KPI associés
        return processusRepository.save(processus);
    }
*/

    @PostMapping("/addp")
    public Processus createProcessuss(@Valid @RequestBody Processus processus) {
        // Vérifier si des KPI ont été fournis
        Set<Kpi> kpis = processus.getKpis();
        Set<Kpi> associatedKpis = new HashSet<>();

        if (kpis != null && !kpis.isEmpty()) {
            // Boucler sur chaque KPI fourni et associer au processus
            for (Kpi kpi : kpis) {
                // Assurer que le KPI existe en base de données en récupérant son ID
                Kpi existingKpi = kpiRepository.findById(kpi.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("KPI not found with ID: " + kpi.getId()));
                // Ajouter le KPI à la liste des KPI associés au processus
                associatedKpis.add(existingKpi);
            }
        }

        // Associer les KPI au processus
        processus.setKpis(associatedKpis);

        // Enregistrer le processus avec les KPI associés
        return processusRepository.save(processus);
    }

    @DeleteMapping("/processus/{id}")
    public ResponseEntity<?> deleteProcessus(@PathVariable Long id) {
        // Vérifier si le processus existe
        Optional<Processus> processusOptional = processusRepository.findById(id);
        if (!processusOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Processus processus = processusOptional.get();

        // Supprimer les KPIs liés au processus dans la table de jointure
        processus.getKpis().clear(); // Effacez simplement la liste des KPIs associés

        // Supprimer le processus de la base de données
        processusRepository.delete(processus);

        return ResponseEntity.ok().build();
    }



    @GetMapping("/processus/{id}")
    public ResponseEntity<Processus> getProcessusById(@PathVariable(value = "id") Long processusId)
            throws ResourceNotFoundException {
        Processus processus = processusRepository.findById(processusId)
                .orElseThrow(() -> new ResourceNotFoundException("Processus not found ::" + processusId));
        return ResponseEntity.ok().body(processus);
    }

}
