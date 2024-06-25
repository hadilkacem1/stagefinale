package com.Telnet.volet.repository;

import com.Telnet.volet.model.Cadran;
import com.Telnet.volet.model.EType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CadranRepository extends JpaRepository<Cadran, Long> {

    List<Cadran> findByVoletId(Long volet_id);
    long countByVoletIdAndType(Long voletId, EType type);

    // Méthode pour compter les faiblesses par volet
    Cadran findByName(String name);
    Optional<Cadran> findByIdAndVoletId(Long id, Long volet_id);
    Long countByTypeAndStartDatePBetween(EType type, LocalDate startDate, LocalDate endDate);
    @Query(value = "SELECT * FROM cadrans RIGHT JOIN volets ON cadrans.volet_id=volets.id and volets.axe=?1", nativeQuery = true)
    List<Cadran> findByAxe(String axe);

    @Query(value = "SELECT input FROM cadrans where input and input IS NOT NULL", nativeQuery = true)
    List<Cadran> findByType(String value);

    @Query(value = "SELECT id,strength FROM cadrans where strength IS NOT NULL ", nativeQuery = true)
    List<Cadran> findStrengths();


    // @Query(value = "SELECT id,strength,volet_id FROM cadrans where strength IS NOT NULL ", nativeQuery = true)
    // List<?> findStrength();

    @Query(value = "SELECT * FROM cadrans where type='STRENGTH' ", nativeQuery = true)
    List<Cadran> findStrength();

    @Query(value = "SELECT * FROM cadrans where type='WEAKNESS' ", nativeQuery = true)
    List<Cadran> findWeakness();

    @Query(value = "SELECT * FROM cadrans where type='OPPORTUNITY' ", nativeQuery = true)
    List<Cadran> findOpportunity();

    @Query(value = "SELECT * FROM cadrans where type='THREAT' ", nativeQuery = true)
    List<Cadran> findThreat();

    long countByType(EType type);

    // Méthode pour compter les faiblesses par volet
}
