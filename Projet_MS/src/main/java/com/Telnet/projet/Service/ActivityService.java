package com.Telnet.projet.Service;



import com.Telnet.projet.models.Activity;
import com.Telnet.projet.models.User;
import com.Telnet.projet.repository.ActivitiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private ActivitiesRepository activitiesRepository;



    public List<Activity> getAllActivities() {
        return activitiesRepository.findAll();
    }

    @Transactional
    public void deleteActivityAssociations(Long activityId) throws ResourceNotFoundException {
        // Vérifie si l'activité existe
        Activity activity = activitiesRepository.findById(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found :: " + activityId));

        // Supprimer l'activité de la liste des activités de chaque utilisateur
        for (User user : activity.getUsers()) {
            user.getActivities().remove(activity);
        }

        // Effacer la liste des utilisateurs associés à cette activité
        activity.getUsers().clear();

        // Supprimer les processus liés à cette activité
        activity.getProcessus().clear();

        // Enregistrer les modifications dans la base de données
        activitiesRepository.save(activity);

        // Enfin, supprimer l'activité elle-même
        activitiesRepository.delete(activity);
    }

}
