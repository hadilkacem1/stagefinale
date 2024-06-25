package com.Telnet.projet.Controller;

import com.Telnet.projet.models.*;
import com.Telnet.projet.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin("*")

public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ActivitiesRepository activityRepository;

    @Autowired
    private ProcessusRepository processusRepository;

    private Set<Kpi> kpis;

    @Autowired
    private kpiRepository kpiRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private KpiHistoryRepository kpiHistoryRepository;




  /*  @GetMapping("/activity/{activityId}/average-kpi-count")
    // ye7ssebeli nbr moyen de kpi for each activity (kpi niveau 2 par activity)
    public double getAverageKpiCountPerActivity(@PathVariable Long activityId) {
        Activity activity = activityRepository.findById(activityId).orElse(null);
        if (activity == null) {
            // handle error: activity not found
        }
        int kpiCount = 0;
        int projectCount = 0;
        for (Project project : activity.getProjects()) {
            for (Processus process : project.getProcessus()) {
                kpiCount += process.getKpis().size();
            }
            projectCount++;
        }
        double averageKpiCount = projectCount > 0 ? ((double) kpiCount) / projectCount : 0.0;

        return averageKpiCount;
    }*/


    @GetMapping("/{projectId}/processus")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public ResponseEntity<List<Processus>> getProcessusByProjectId(@PathVariable Long projectId) {
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));

            List<Processus> processusList = project.getProcessus();
            return new ResponseEntity<>(processusList, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/projetDetails/{id}")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public ResponseEntity<?> getDetails(@PathVariable Long id) {
        Optional<Project> projet = projectRepository.findById(id);
        if (!projet.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Project projetDetails = new Project();
        projetDetails.setId(projet.get().getId());
        projetDetails.setName(projet.get().getName());
        projetDetails.setType(projet.get().getType());
        projetDetails.setActivity(projet.get().getActivity());
        projetDetails.setProcessus(projet.get().getProcessus());
        projetDetails.setCli(projet.get().getCli());

        return ResponseEntity.ok(projetDetails);
    }

    @GetMapping("/projetDetailsByKpi/{kpiId}")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public ResponseEntity<?> getDetailsByKpi(@PathVariable Long kpiId) {
        List<Project> projets = projectRepository.findByKpisId(kpiId);
        if (projets.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(projets);
    }


    @GetMapping("/{projectId}/processusAndKpi")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public ResponseEntity<Map<String, List<String>>> getProcessusAndKpiByProjectId(@PathVariable Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));

        Map<String, List<String>> processusAndKpiMap = new HashMap<>();

        for (Processus processus : project.getProcessus()) {
            List<String> kpiNames = processus.getKpis().stream()
                    .map(Kpi::getName)
                    .collect(Collectors.toList());
            processusAndKpiMap.put(processus.getName(), kpiNames);
        }

        return ResponseEntity.ok(processusAndKpiMap);
    }


    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public ResponseEntity<?> deleteProject(@PathVariable(value = "id") Long projectId) {
        try {
            // Rechercher le projet dans la base de données
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));

            // Supprimer l'association entre les projets et les KPI
            project.setKpis(null);
            projectRepository.save(project);

            // Supprimer le projet de la base de données
            projectRepository.delete(project);

            // Retourner une réponse vide pour indiquer que la suppression a réussi
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            // Gérer l'erreur si le projet n'est pas trouvé
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Gérer toute autre erreur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete project: " + e.getMessage());
        }
    }


    @PutMapping("/updateproject/{id}")
//    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public ResponseEntity<?> updateProject(@PathVariable(value = "id") Long projectId, @RequestBody Project projectRequest) {
        try {
            // Recherche du projet dans la base de données
            Project existingProject = projectRepository.findById(projectId)
                    .orElseThrow(() -> new EntityNotFoundException("Project not found with id: " + projectId));

            // Récupération des données du projet à mettre à jour
            String projectName = projectRequest.getName();
            String projectType = projectRequest.getType();
            DateRangee projectDate = projectRequest.getProjectDate();
            Client client = projectRequest.getCli();
            List<Processus> processusList = projectRequest.getProcessus();

            // Vérification de l'existence du client sélectionné
            Client existingClient = clientRepository.findById(client.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + client.getId()));

            // Vérification des processus
            if (processusList == null || processusList.isEmpty()) {
                return ResponseEntity.badRequest().body("Processus list cannot be empty");
            }

            // Mise à jour des champs du projet existant avec les nouvelles valeurs
            existingProject.setName(projectName);
            existingProject.setType(projectType);
            if (projectDate != null) {
                existingProject.getProjectDate().setStartDate(projectDate.getStartDate());
                existingProject.getProjectDate().setEndDate(projectDate.getEndDate());
            }
            existingProject.setCli(existingClient);
            existingProject.setProcessus(processusList);

            // Enregistrement des modifications dans la base de données
            Project updatedProject = projectRepository.save(existingProject);

            return ResponseEntity.ok(updatedProject);
        } catch (EntityNotFoundException e) {
            // Gestion de l'erreur si le projet ou le client n'est pas trouvé
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            // Gestion de l'erreur si les données du projet sont invalides
            return ResponseEntity.badRequest().body("Invalid project data: " + e.getMessage());
        } catch (Exception e) {
            // Gestion de toutes les autres exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update project: " + e.getMessage());
        }
    }


    @PostMapping("/create-project")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public ResponseEntity<?> createProject(@RequestBody Project projectRequest) {
        try {
            String projectName = projectRequest.getName();
            String projectType = projectRequest.getType();

            // Modification ici : Extraction des valeurs startDate et endDate de projectDate
            Date startDate = projectRequest.getProjectDate().getStartDate();
            Date endDate = projectRequest.getProjectDate().getEndDate();

            Activity activity = projectRequest.getActivity();
            Client client = projectRequest.getCli();
            List<Processus> processusList = projectRequest.getProcessus();

            if (client == null) {
                return ResponseEntity.badRequest().body("Client cannot be null");
            }

            if (processusList == null || processusList.isEmpty()) {
                return ResponseEntity.badRequest().body("Processus list cannot be empty");
            }

            Long activityId = activity.getId();

            // Modification ici : Utilisation des valeurs startDate et endDate pour créer un nouveau projet
            Project newProject = new Project(projectName, projectType, new DateRangee(startDate, endDate), processusList, client, null, activity);

            newProject.setProcessus(processusList);
            Set<Kpi> kpiSet = retrieveKpisForProcessusList(processusList);
            newProject.setKpis(kpiSet);

            Project savedProject = projectRepository.save(newProject);

            return ResponseEntity.ok(savedProject);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid project data: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create project: " + e.getMessage());
        }
    }


    private Set<Kpi> retrieveKpisForProcessusList(List<Processus> processusList) {
        Set<Kpi> kpiList = new HashSet<>();
        for (Processus processus : processusList) {
            List<Kpi> kpis = kpiRepository.findByProcessusList(processus);
            kpiList.addAll(kpis);
        }
        return kpiList;
    }


    @GetMapping("/projectbyid/{id}")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public ResponseEntity<Project> getProjectById(@PathVariable(value = "id") Long projectId)
            throws ResourceNotFoundException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found ::" + projectId));
        return ResponseEntity.ok().body(project);
    }


    @GetMapping("/processus/{id}/kpi-average")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    // ye7ssebeli nbr moyen de kpi for each processus (kpi niveau 3 par processus)
    public double getKpiAverageForProcessus(@PathVariable Long id) {
        Optional<Processus> processus = processusRepository.findById(id);
        if (processus.isPresent()) {
            int kpiCount = 0;
            for (Kpi kpi : processus.get().getKpis()) {
                kpiCount++;
            }
            return (double) kpiCount;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Processus not found");
        }


    }














  /*  @PostMapping("/{activity_id}/add")
    public Project createProject(@PathVariable(value = "activity_id") Long activity_id, @Valid @RequestBody Project project) throws ParseException {
        logger.info("create Activity triggered !!!");
        if(activity_id == null) {
            throw new IllegalArgumentException("Activity ID cannot be null");
        }

        logger.info("{}", activityRepository.findById(activity_id).get().getName());
        Optional<Activity> activity = activityRepository.findById(activity_id);
        List<Processus> processus = project.getProcessus();


        // Save the Project instance
        Project currentProject = new Project(project.getName(), project.getType(), project.getClient(), project.getProjectDate(), null, processus, null, kpis, null);
        currentProject.setActivity(activity.get());
        return projectRepository.save(currentProject);
    }
*/


    @GetMapping("/{activity_id}/list")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public List<Project> getProjectListIntoActivityId(@PathVariable("activity_id") Long activityId) {
        return projectRepository.findByActivityId(activityId);
    }


    @GetMapping("/{activity_id}/list/{id}")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public ResponseEntity<Project> getProjectByIdIntoActivityId(@PathVariable(value = "id") Long projectId)
            throws ResourceNotFoundException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found ::" + projectId));
        return ResponseEntity.ok().body(project);
    }


    @GetMapping("/activities/{activityId}/processus")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public ResponseEntity<List<Processus>> getProcessusByActivityId(@PathVariable Long activityId) {
        List<Processus> processusList = processusRepository.findByActivities_Id(activityId);
        return ResponseEntity.ok().body(processusList);
    }

  /*  @GetMapping("/{activity_id}/{id}/{procId}/kpiList")
    public ResponseEntity<Set<Kpi>> getKiListIntoProcessusByIdIntoProjectIdandActivityId(@PathVariable(value = "activity_id") Long activityId,
                                                                                         @PathVariable(value = "id") Long projectId,
                                                                                         @PathVariable(value = "procId") Long processusId)
            throws ResourceNotFoundException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found ::" + projectId));

        // Check if the project belongs to the given activity
        if (!project.getActivity().getId().equals(activityId)) {
            throw new ResourceNotFoundException("Project not found in activity ::" + activityId);
        }

        Processus processus = project.getProcessus().stream()
                .filter(p -> p.getId().equals(processusId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Processus not found ::" + processusId));

        Set<Kpi> kpiList = processus.getKpis();
        return ResponseEntity.ok().body(kpiList);
    } */


    @GetMapping("/listp")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public List<Project> getProjectList() {
        return projectRepository.findAll();
    }


    @GetMapping("/projects-by-date")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE') or hasAuthority('ROLE_CHEFDEPROJET')")
    public List<Project> getProjectsByDate(@RequestParam("startDate") String startDateStr,
                                           @RequestParam("endDate") String endDateStr) {
        LocalDateTime startDateTime = LocalDateTime.parse(startDateStr, DateTimeFormatter.ISO_DATE_TIME);
        java.sql.Date startDate = java.sql.Date.valueOf(startDateTime.toLocalDate());

        LocalDateTime endDateTime = LocalDateTime.parse(endDateStr, DateTimeFormatter.ISO_DATE_TIME);
        java.sql.Date endDate = java.sql.Date.valueOf(endDateTime.toLocalDate());

        List<Project> projectsByDate = projectRepository.findProjectsByDate(startDate, endDate);

        // Vous n'avez pas besoin de boucler pour ajouter les KPI à chaque projet
        // Il semble que votre repository devrait déjà retourner les KPI associés à chaque projet

        return projectsByDate;
    }

    @GetMapping("/count/type/{type}")
    @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE')")
    public ResponseEntity<Map<String, Integer>> getProjectCountByType(@PathVariable String type) {
        Map<String, Integer> result = new HashMap<>();
        if (type.equals("Régie") || type.equals("Forfait")) {
            int count = projectRepository.countByType(type);
            result.put(type, count);
            return ResponseEntity.ok().body(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }



    private boolean isProjectWithinDateRange(Project project, LocalDate startDate, LocalDate endDate) {
        DateRangee projectDate = project.getProjectDate();
        if (projectDate == null) {
            return false;
        }

        LocalDate projectStartDate = projectDate.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate projectEndDate = projectDate.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return !startDate.isBefore(projectStartDate) && !endDate.isAfter(projectEndDate);
    }

    @GetMapping("/total-projets-par-client")
   // @PreAuthorize("hasAuthority('ROLE_RESPONSABLEQUALITE')")
    public ResponseEntity<List<Object[]>> getTotalProjetsParClient() {
        List<Object[]> data = projectRepository.getTotalProjetsParClient();
        return ResponseEntity.ok(data);
    }









}




