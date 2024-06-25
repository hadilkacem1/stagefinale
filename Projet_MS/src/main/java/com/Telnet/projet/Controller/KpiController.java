package com.Telnet.projet.Controller;

import com.Telnet.projet.Service.KpiHistoryService;
import com.Telnet.projet.Service.KpiService;
import com.Telnet.projet.models.*;
import com.Telnet.projet.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/kpi")
public class KpiController {

    @Autowired
    kpiRepository kpiRepository;

    @Autowired
    ProcessusRepository processusRepository;



    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    KpiHistoryRepository kpiHistoryRepository;

    @Autowired
    KpiService kpiService;

    @Autowired
    KpiHistoryService kpiHistoryService;
    @Autowired
    ActivitiesRepository activitiesRepository;

    @GetMapping("/list")
    public List<Kpi> getKpiList() {
        return kpiRepository.findAll();
    }

    @GetMapping("/getkpi/{id}")
    public ResponseEntity<Kpi> getKpiById(@PathVariable Long id) {
        Kpi kpi = kpiService.getKpiById(id);
        if (kpi != null) {
            return ResponseEntity.ok(kpi);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/processus/{processusIds}")
    public List<Kpi> getKpisByProcessusIds(@PathVariable List<Long> processusIds) {
        // Retrieve the Processus entities based on the provided IDs
        List<Processus> processusList = processusRepository.findAllById(processusIds);

        // Call the repository to retrieve the KPIs based on the Processus entities
        List<Kpi> kpis = kpiRepository.findByProcessusListIn(processusList);
        return kpis;
    }
    @PostMapping("/update/{historyId}")
    public ResponseEntity<KpiHistory> updateKpiHistory(@PathVariable Long historyId, @RequestBody KpiHistoryRequest requestBody) {
        Optional<KpiHistory> optionalKpiHistory = kpiHistoryRepository.findById(historyId);
        if (optionalKpiHistory.isPresent()) {
            KpiHistory kpiHistory = optionalKpiHistory.get();

            // Extraire les données de la requête
            LocalDate startDate = requestBody.getStartDateP();
            LocalDate endDate = requestBody.getEndDateP();
            Integer value = requestBody.getValue();

            // Mettre à jour les champs de l'historique
            kpiHistory.setStartDateP(startDate);
            kpiHistory.setEndDateP(endDate);
            kpiHistory.setValue(value);

            // Sauvegarder et retourner l'historique mis à jour
            return ResponseEntity.ok(kpiHistoryRepository.save(kpiHistory));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


 /*   @PostMapping("/update/{historyId}")
    public ResponseEntity<KpiHistory> updateKpiHistory(@PathVariable Long historyId, @RequestBody KpiHistoryRequest requestBody) {
        // Extraire les données de la requête
        LocalDate startDate = requestBody.getStartDateP();
        LocalDate endDate = requestBody.getEndDateP();
        Integer value = requestBody.getValue();

        // Créer une nouvelle instance de KpiHistory avec les nouvelles données
        KpiHistory newKpiHistory = new KpiHistory();
        newKpiHistory.setStartDateP(startDate);
        newKpiHistory.setEndDateP(endDate);
        newKpiHistory.setValue(value);

        // Sauvegarder la nouvelle entrée dans la base de données
        KpiHistory savedKpiHistory = kpiHistoryRepository.save(newKpiHistory);

        // Retourner la nouvelle entrée sauvegardée
        return ResponseEntity.ok(savedKpiHistory);
    }
*/


    @GetMapping("/api/kpi/{id}/value")
    public ResponseEntity<Integer> getKpiValueById(@PathVariable Long id) {
        // Appelez directement la méthode du repository pour obtenir la dernière valeur du KPI
        Optional<KpiHistory> optionalKpiHistory = kpiHistoryRepository.findFirstByKpiIdOrderByStartDatePDesc(id);

        // Vérifiez si la valeur est présente
        if (optionalKpiHistory.isPresent()) {
            KpiHistory kpiHistory = optionalKpiHistory.get();
            Integer kpiValue = kpiHistory.getValue();
            return ResponseEntity.ok().body(kpiValue);
        } else {
            // Gérez le cas où aucune valeur n'est trouvée pour le KPI spécifié
            return ResponseEntity.notFound().build();
        }
    }




    @GetMapping("/{kpiId}/latest-history")
    public ResponseEntity<Map<String, Object>> getLatestKpiHistory(@PathVariable Long kpiId) {
        KpiHistory latestKpiHistory = kpiHistoryService.getLatestKpiHistoryForKpi(kpiId);

        if (latestKpiHistory == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("value", latestKpiHistory.getValue());
        response.put("startDateP", latestKpiHistory.getStartDateP());
        response.put("endDateP", latestKpiHistory.getEndDateP());

        return ResponseEntity.ok(response);
    }
    @PutMapping("/kpihistories/{historyId}")
    public ResponseEntity<KpiHistory> updateKpiHistory(@PathVariable Long historyId, @RequestBody KpiHistory updatedHistory) {
        KpiHistory existingHistory = kpiHistoryService.getKpiHistoryById(historyId);

        if (existingHistory == null) {
            return ResponseEntity.notFound().build();
        }

        // Mettre à jour les propriétés de l'historique existant avec les nouvelles valeurs
        existingHistory.setValue(updatedHistory.getValue());
        existingHistory.setStartDateP(updatedHistory.getStartDateP());
        existingHistory.setEndDateP(updatedHistory.getEndDateP());

        // Enregistrer les modifications dans la base de données
        KpiHistory savedHistory = kpiHistoryService.saveKpiHistory(existingHistory);

        return ResponseEntity.ok(savedHistory);
    }



    @PostMapping("/save/{kpiId}/history")
    public KpiHistory createKpiHistoryByKpiIdAndProjectId(@PathVariable Long kpiId,
                                                          @RequestParam Long projectId,
                                                          @RequestBody KpiHistoryRequest requestBody) throws ResourceNotFoundException {
        // Extraire les données de la requête
        LocalDate startDate = requestBody.getStartDateP();
        LocalDate endDate = requestBody.getEndDateP();
        Integer value = requestBody.getValue();

        // Récupérer l'objet Projet correspondant à l'ID fourni
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        // Récupérer l'objet KPI correspondant à l'ID fourni dans le projet
        Kpi kpi = project.getKpis().stream()
                .filter(k -> k.getId().equals(kpiId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Kpi not found with id: " + kpiId));

        // Créer un nouvel objet KpiHistory
        KpiHistory kpiHistory = new KpiHistory();
        kpiHistory.setStartDateP(startDate);
        kpiHistory.setEndDateP(endDate);
        kpiHistory.setValue(value);
        kpiHistory.setKpi(kpi);
        kpiHistory.setKpiName(kpi.getName());
        kpiHistory.setObjectif(kpi.getObjectif());
        kpiHistory.setProject(project);

        // Ajouter l'historique à la liste d'historique du projet
        project.getHistoryList().add(kpiHistory);

        // Sauvegarder le projet avec l'historique mis à jour
        projectRepository.save(project);

        // Sauvegarder et retourner l'objet KpiHistory créé
        return kpiHistoryRepository.save(kpiHistory);
    }

    @GetMapping("/projects/{projectId}/kpis/{kpiId}/history")
    public List<KpiHistory> getKpiHistoryByProjectAndKpiId(@PathVariable Long projectId, @PathVariable Long kpiId) {
        return kpiService.getKpiHistoryByProjectAndKpiId(projectId, kpiId);
    }

    @GetMapping("/kpis/{kpiId}/history")
    public ResponseEntity<List<KpiHistory>> getKpiHistoryByKpiId(@PathVariable Long kpiId) {
        List<KpiHistory> kpiHistoryList = kpiService.getKpiHistoryByKpiId(kpiId);
        if (kpiHistoryList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(kpiHistoryList);
        }
    }

    @PostMapping("/result/{kpiId}/history")
    public KpiHistory createKpiHistoryByKpiId(@PathVariable Long kpiId,
                                              @RequestBody KpiHistoryRequest requestBody) throws ResourceNotFoundException {
        // Extraire les données de la requête
        LocalDate startDate = requestBody.getStartDateP();
        LocalDate endDate = requestBody.getEndDateP();
        Integer value = requestBody.getValue();

        // Valider les données reçues dans la requête
        if (startDate == null || endDate == null || value == null) {
            throw new IllegalArgumentException("Invalid request body");
        }

        // Récupérer l'objet KPI correspondant à l'ID fourni
        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("Kpi not found with id: " + kpiId));

        // Créer un nouvel objet KpiHistory
        KpiHistory kpiHistory = new KpiHistory();
        kpiHistory.setStartDateP(startDate);
        kpiHistory.setEndDateP(endDate);
        kpiHistory.setValue(value);
        kpiHistory.setKpi(kpi);
        kpiHistory.setKpiName(kpi.getName());
        kpiHistory.setObjectif(kpi.getObjectif());

        // Sauvegarder et retourner l'objet KpiHistory créé
        return kpiHistoryRepository.save(kpiHistory);
    }

    @PutMapping("/result/{kpiId}/history/{historyId}")
    public KpiHistory updateKpiHistoryByKpiId(@PathVariable Long kpiId,
                                              @PathVariable Long historyId,
                                              @Valid @RequestBody KpiHistoryRequest requestBody) throws ResourceNotFoundException {
        // Récupérer l'objet KpiHistory correspondant à l'ID fourni
        KpiHistory kpiHistory = kpiHistoryRepository.findById(historyId)
                .orElseThrow(() -> new ResourceNotFoundException("KpiHistory not found with id: " + historyId));

        // Valider les données reçues dans la requête
        if (requestBody == null || requestBody.getStartDateP() == null || requestBody.getEndDateP() == null || requestBody.getValue() == null) {
            throw new IllegalArgumentException("Invalid request body");
        }

        // Récupérer l'objet KPI correspondant à l'ID fourni
        Kpi kpi = kpiRepository.findById(kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("Kpi not found with id: " + kpiId));

        // Mettre à jour les propriétés de l'objet KpiHistory avec les nouvelles valeurs
        kpiHistory.setStartDateP(requestBody.getStartDateP());
        kpiHistory.setEndDateP(requestBody.getEndDateP());
        kpiHistory.setValue(requestBody.getValue());
        kpiHistory.setKpi(kpi);
        kpiHistory.setKpiName(kpi.getName());
        kpiHistory.setObjectif(kpi.getObjectif());

        // Sauvegarder et retourner l'objet KpiHistory modifié
        return kpiHistoryRepository.save(kpiHistory);
    }






    @GetMapping("/projects/{projectId}/kpis/history")
    public ResponseEntity<List<KpiHistory>> getKpiHistoryByProjectId(@PathVariable Long projectId) {
        List<KpiHistory> kpiHistoryList = kpiService.getKpiHistoryByProjectId(projectId);
        if (kpiHistoryList.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(kpiHistoryList);
        }
    }

    @GetMapping("/{kpiId}/objectif")
    public ResponseEntity<Integer> getKpiObjectifById(@PathVariable(value = "kpiId") Long kpiId) {
        Optional<Kpi> optionalKpi = kpiRepository.findById(kpiId);
        if (optionalKpi.isPresent()) {
            Kpi kpi = optionalKpi.get();
            return ResponseEntity.ok(kpi.getObjectif());
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @GetMapping("/{kpiId}/history")
    public List<KpiHistory> getKpiHistoryBykpiId(@PathVariable(value = "kpiId") Long kpiId) {
        return kpiHistoryService.getKpiHistoryByKpiId(kpiId);
    }

    @GetMapping("/history/{id}")
    public ResponseEntity<KpiHistory> getKpiHistoryById(@PathVariable("id") Long id) {
        Optional<KpiHistory> optionalKpiHistory = kpiHistoryRepository.findById(id);
        return optionalKpiHistory.map(kpiHistory ->
                        new ResponseEntity<>(kpiHistory, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/historyId/{kpiId}")
    public ResponseEntity<List<Long>> getHistoryIdsByKpiId(@PathVariable Long kpiId) {
        List<Long> historyIds = kpiHistoryService.getHistoryIdsByKpiId(kpiId);
        if (!historyIds.isEmpty()) {
            return new ResponseEntity<>(historyIds, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/activity/{activityId}/kpis")
    public ResponseEntity<List<Kpi>> getKpiByActivityId(@PathVariable Long activityId) {
        // Trouver l'activité par son ID
        Optional<Activity> optionalActivity = activitiesRepository.findById(activityId);
        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();

            // Récupérer tous les processus associés à cette activité
            Set<Processus> processusSet = activity.getProcessus();

            // Initialiser une liste pour stocker tous les Kpi associés à ces processus
            List<Kpi> kpis = new ArrayList<>();

            // Parcourir tous les processus et récupérer leurs Kpi
            for (Processus processus : processusSet) {
                kpis.addAll(processus.getKpis());
            }

            return ResponseEntity.ok(kpis);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/kpis/between-dates")
    public ResponseEntity<List<String>> getLatestKpiNamesBetweenDates(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        // Appelez le service pour récupérer les historiques de KPI entre les dates spécifiées
        List<KpiHistory> kpiHistories = kpiHistoryService.getLatestKpiHistoriesBetweenDates(startDate, endDate);

        // Créez une liste pour stocker les noms de KPI
        List<String> kpiNames = new ArrayList<>();

        // Parcourez les historiques de KPI pour extraire les noms de KPI
        for (KpiHistory kpiHistory : kpiHistories) {
            // Ajoutez le nom du KPI à la liste
            kpiNames.add(kpiHistory.getKpiName());
        }

        // Vérifiez si des noms de KPI ont été trouvés
        if (kpiNames.isEmpty()) {
            // Si aucun nom de KPI n'est trouvé, renvoyer une réponse 404 (Not Found)
            return ResponseEntity.notFound().build();
        } else {
            // Si des noms de KPI sont trouvés, renvoyer une réponse 200 (OK) avec les noms des KPI
            return ResponseEntity.ok(kpiNames);
        }
    }




    @PostMapping("addKpiToProcessus/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Processus> addKpiToProcessus(@PathVariable("id") Long processusId, @RequestBody Kpi kpi) {
        Optional<Processus> optionalProcessus = processusRepository.findById(processusId);
        if (!optionalProcessus.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Processus processus = optionalProcessus.get();
        Set<Kpi> kpis = processus.getKpis();
        kpi = kpiRepository.save(kpi);
        kpis.add(kpi);
        processus.setKpis(kpis);
        processus = processusRepository.save(processus);
        return ResponseEntity.ok(processus);
    }


}