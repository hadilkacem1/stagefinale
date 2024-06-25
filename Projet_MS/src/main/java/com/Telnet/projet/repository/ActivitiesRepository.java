package com.Telnet.projet.repository;

import com.Telnet.projet.models.Activity;
import com.Telnet.projet.models.Processus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ActivitiesRepository  extends JpaRepository<Activity,Long> {
    Activity findByName(String name);
  //  List<Activity> findByUsers_Id(Integer userId);
    @Query("SELECT a.name FROM Activity a")
    List<String> getActivityNames();

    @Query("SELECT a.processus FROM Activity a WHERE a.id = :activityId")
    List<Processus> findProcessusByActivityId(@Param("activityId") Long activityId);
  List<Activity> findByUsers_Id(Integer userId);

}
