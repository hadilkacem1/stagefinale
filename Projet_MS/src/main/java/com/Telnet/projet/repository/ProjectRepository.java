package com.Telnet.projet.repository;


import com.Telnet.projet.models.Activity;
import com.Telnet.projet.models.Kpi;
import com.Telnet.projet.models.Processus;
import com.Telnet.projet.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

//	List<Project> findByActivity(Activity activity);

    Integer countProjectByactivityId(Long activityId);

    Integer countProjectByprojectDate(Long id);

    Integer countProjectByType(String name);


    Project findByName(String key);
    List<Project> findByKpisId(Long kpiId);

    Long countByActivityAndProjectDate(Activity activity, Date projectDate);

    @Query("SELECT a FROM Activity a JOIN a.projects p WHERE p.id = :projectId")
    List<Activity> findActivitiesByProjectId(@Param("projectId") Long projectId);

    List<Project> findByActivityId(Long activityId);

    List<Project> findByProcessus(Processus processus);

    List<Project> findAllByCliIsNotNull();

    List<Project> findByActivity(Activity activity);
    int countByType(String type);
    Set<Kpi> findKpisById(Long projectId);

    @Query("SELECT p.cli.name, COUNT(p) FROM Project p GROUP BY p.cli.name")
    List<Object[]> getTotalProjetsParClient();

    @Query("SELECT p FROM Project p WHERE p.projectDate.startDate >= :startDate AND p.projectDate.endDate <= :endDate")
    List<Project> findProjectsByDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
