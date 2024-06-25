package com.Telnet.projet.repository;

import com.Telnet.projet.models.Kpi;
import com.Telnet.projet.models.Processus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface kpiRepository  extends JpaRepository<Kpi, Long> {
    Optional<Kpi> findById(Long id);

    List<Kpi> findByProcessusList(Processus processus);

    List<Kpi> findByProcessusListIn(List<Processus> processusList);


}
