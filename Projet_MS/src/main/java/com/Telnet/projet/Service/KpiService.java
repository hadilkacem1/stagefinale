package com.Telnet.projet.Service;

import com.Telnet.projet.models.Kpi;
import com.Telnet.projet.models.KpiHistory;
import com.Telnet.projet.repository.KpiHistoryRepository;
import com.Telnet.projet.repository.kpiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KpiService {
    @Autowired
    private kpiRepository kpiRepository;

    @Autowired
    KpiHistoryRepository kpiHistoryRepository;


    public KpiService(kpiRepository kpiRepository) {
        this.kpiRepository = kpiRepository;
    }


    public Kpi createKpi(Kpi kpi) {
        return kpiRepository.save(kpi);
    }

    public Kpi updateKpi(Kpi kpi) {
        return kpiRepository.save(kpi);
    }

    public void deleteKpi(Long kpiId) {
        kpiRepository.deleteById(kpiId);
    }

    public List<Kpi> getKpiList() {
        return kpiRepository.findAll();
    }

    public Kpi getKpiById(Long kpiId) throws ResourceNotFoundException {
        return kpiRepository.findById(kpiId)
                .orElseThrow(() -> new ResourceNotFoundException("Kpi not found ::" + kpiId));
    }
    public List<KpiHistory> getKpiHistoryByProjectAndKpiId(Long projectId, Long kpiId) {
        return kpiHistoryRepository.findByProjectIdAndKpiId(projectId, kpiId);
    }

    public List<KpiHistory> getKpiHistoryByKpiId(Long kpiId) {
        return kpiHistoryRepository.findByKpiId(kpiId);
    }
    public List<KpiHistory> getKpiHistoryByProjectId(Long projectId) {
        return kpiHistoryRepository.findByProjectId(projectId);
    }




}