package com.Telnet.projet.Service;

import com.Telnet.projet.models.KpiHistory;
import com.Telnet.projet.repository.KpiHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class KpiHistoryService {

    @Autowired
    private KpiHistoryRepository kpiHistoryRepository;

    public KpiHistory createKpiHistory(KpiHistory kpiHistory) {
        return kpiHistoryRepository.save(kpiHistory);
    }

    public List<KpiHistory> getKpiHistoryByKpiId(Long kpiId) {
        return kpiHistoryRepository.findByKpiId(kpiId);
    }

    public KpiHistory getLatestKpiHistoryForKpi(Long kpiId) {
        return kpiHistoryRepository.findTopByKpiIdOrderByEndDatePDesc(kpiId);
    }


    public List<KpiHistory> getLatestKpiHistoriesBetweenDates(LocalDate startDate, LocalDate endDate) {
        // Utilisez la méthode du repository pour récupérer les historiques des KPIs entre les dates spécifiées
        return kpiHistoryRepository.findByStartDatePBetweenOrderByStartDatePDesc(startDate, endDate);
    }
    public KpiHistory saveKpiHistory(KpiHistory kpiHistory) {
        return kpiHistoryRepository.save(kpiHistory);
    }

    public List<Long> getHistoryIdsByKpiId(Long kpiId) {
        return kpiHistoryRepository.findHistoryIdsByKpiId(kpiId);
    }

    public KpiHistory getKpiHistoryById(Long historyId) {
        return kpiHistoryRepository.findById(historyId).orElse(null);
    }

    public List<KpiHistory> getByProjectIdAndKpiId(Long projectId, Long kpiId) {
        // TODO Auto-generated method stub
        return null;
    }





}