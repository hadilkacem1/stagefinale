package com.Telnet.projet.repository;
import com.Telnet.projet.models.Activity;
import com.Telnet.projet.models.Processus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessusRepository  extends JpaRepository<Processus, Long> {
    List<Processus> findByActivitiesContains(Activity activity);

    List<Processus> findByActivities_Id(Long activityId);
    Optional<Processus> findByActivitiesId(Long activityId);
}
