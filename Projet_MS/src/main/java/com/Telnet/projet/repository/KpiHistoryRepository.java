package com.Telnet.projet.repository;


import com.Telnet.projet.models.Kpi;
import com.Telnet.projet.models.KpiHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KpiHistoryRepository extends JpaRepository<KpiHistory, Long> {

    List<KpiHistory> findByKpiId(Long kpiId);

    @Query("SELECT k FROM KpiHistory k WHERE k.startDateP BETWEEN :startDate AND :endDate AND k.value < k.objectif")
    List<KpiHistory> findKpisBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


//	KpiHistory findTopByKpiIdOrderByPeriodeDesc(Long kpiId);

//	KpiHistory findTopByKpiNameOrderByPeriodeDesc(String kpiName);

//	KpiHistory findFirstByKpiIdAndProjectIdOrderByPeriodeDesc(Long kpiId, Long projectId);


    static List<KpiHistory> findByProjectIdAndPeriodeBetween(Long id, java.util.Date startDate,
                                                             java.util.Date endDate) {
        // TODO Auto-generated method stub
        return null;
    }


    static List<KpiHistory> findByPeriod(Date startDate, Date endDate) {
        // TODO Auto-generated method stub
        return null;
    }

    List<KpiHistory> findByProjectIdAndKpiId(Long projectId, Long kpiId);

    List<KpiHistory> findByProjectId(Long projectId);
    KpiHistory findTopByKpiIdOrderByEndDatePDesc(Long kpiId);
    List<KpiHistory> findByStartDatePBetweenOrderByStartDatePDesc(LocalDate startDate, LocalDate endDate);
    KpiHistory findFirstByKpiIdAndProjectIdOrderByStartDatePDesc(Long kpiId, Long projectId);

    List<KpiHistory> findByKpiIdAndProjectId(Long kpiId, Long projectId);

    KpiHistory findTopByKpiIdAndProjectIdOrderByStartDatePDesc(Long kpiId, Long projectId);

    @Query("SELECT DISTINCT kpiHistory.kpi FROM KpiHistory kpiHistory WHERE kpiHistory.startDateP >= :startDate AND kpiHistory.endDateP <= :endDate")
    List<Kpi> findKpis(LocalDate startDate, LocalDate endDate);

    @Query("SELECT kh FROM KpiHistory kh WHERE kh.kpi.id = :kpiId AND kh.value = :value")
    KpiHistory findByKpiIdAndValue(@Param("kpiId") Long kpiId, @Param("value") Double value);

    @Query("SELECT kh.id FROM KpiHistory kh WHERE kh.kpi.id = ?1")
    List<Long> findHistoryIdsByKpiId(Long kpiId);
    Optional<KpiHistory> findFirstByKpiIdOrderByStartDatePDesc(Long kpiId);
}