package com.Telnet.projet.Controller;


import com.Telnet.projet.Service.ActivityService;
import com.Telnet.projet.models.Activity;
import com.Telnet.projet.models.Processus;
import com.Telnet.projet.repository.ActivitiesRepository;
import com.Telnet.projet.repository.ProcessusRepository;
import com.Telnet.projet.repository.ProjectRepository;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;



@RestController
@CrossOrigin("*")





    public class ActivitiesController {


    @Autowired
    private ProjectRepository projectRepository;


    @Autowired
    private ActivityService activityService;

    @Autowired

    private ProcessusRepository processusRepository;

    @Autowired
    private ActivitiesRepository activitiesRepository;


    @GetMapping("/activities")
    public ResponseEntity<List<Activity>> getActivities() {
        List<Activity> activities = activitiesRepository.findAll();
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/activity/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable(value = "id") Long activitiesId)
            throws ResourceNotFoundException {
        Activity activities = activitiesRepository.findById(activitiesId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found ::" + activitiesId));
        return ResponseEntity.ok().body(activities);
    }


    @GetMapping("/user/activity/{userId}")
    public ResponseEntity<List<Activity>> getActivitiesByUserId(@PathVariable(value = "userId") Integer userId)
            throws ResourceNotFoundException {
        List<Activity> activities = activitiesRepository.findByUsers_Id(userId);
        if (activities.isEmpty()) {
            throw new ResourceNotFoundException("Activities not found for user with ID :: " + userId);
        }
        return ResponseEntity.ok().body(activities);
    }

    @GetMapping("/project/{projectId}/activities")
    public ResponseEntity<List<String>> getActivitiesNamesByProjectId(@PathVariable Long projectId) {
        List<Activity> activities = projectRepository.findActivitiesByProjectId(projectId);
        if (!activities.isEmpty()) {
            List<String> activityNames = activities.stream()
                    .map(Activity::getName) // Utilisez la méthode getName() pour obtenir le nom de chaque activité
                    .collect(Collectors.toList());
            return ResponseEntity.ok(activityNames);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/add")
    public ResponseEntity<Activity> createActivity(@Valid @RequestBody Activity activity) {
        Set<Processus> processusSet = activity.getProcessus();
        if (processusSet != null) {
            for (Processus processus : processusSet) {
                processus.getActivities().add(activity);
            }
        }
        // Assuming 'activitiesRepository' is a Spring Data repository for Activity
        Activity savedActivity = activitiesRepository.save(activity);
        return new ResponseEntity<>(savedActivity, HttpStatus.CREATED);
    }


    @PutMapping("/updateactivity/{id}")
    public ResponseEntity<Activity> updateActivity(
            @PathVariable(value = "id") Long activityId,
            @RequestBody Activity activityDetails) throws ResourceNotFoundException {
        Activity activity = activitiesRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found :: " + activityId));

        // Update the activity properties
        activity.setName(activityDetails.getName());
        activity.setDescription(activityDetails.getDescription());

        // Retrieve the processus IDs from the request payload
        Set<Processus> processusSet = new HashSet<>();
        for (Processus processus : activityDetails.getProcessus()) {
            Long processusId = processus.getId();
            Processus existingProcessus = processusRepository.findById(processusId)
                    .orElseThrow(() -> new ResourceNotFoundException("Processus not found :: " + processusId));
            processusSet.add(existingProcessus);
        }
        activity.setProcessus(processusSet);

        final Activity updatedActivity = activitiesRepository.save(activity);
        return ResponseEntity.ok(updatedActivity);
    }


    @DeleteMapping("/activity/{id}")
    @Transactional
    public ResponseEntity<String> deleteActivityAssociations(@PathVariable(value = "id") Long activityId) {
        try {
            activityService.deleteActivityAssociations(activityId);
            return ResponseEntity.ok("Associations for activity with ID " + activityId + " deleted successfully.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


}