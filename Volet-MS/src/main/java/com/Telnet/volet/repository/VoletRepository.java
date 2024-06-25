package com.Telnet.volet.repository;


import com.Telnet.volet.model.EAxe;
import com.Telnet.volet.model.Volet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoletRepository  extends JpaRepository<Volet, Long> {

    List<Volet> findByAxe(EAxe axe);
    long countByAxe(EAxe axe);
   // long countVoletsByCadrans_Type(EType type);
}
