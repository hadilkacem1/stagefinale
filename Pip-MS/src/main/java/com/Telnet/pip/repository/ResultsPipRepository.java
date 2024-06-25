package com.Telnet.pip.repository;

import com.Telnet.pip.model.ResultsPip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultsPipRepository extends JpaRepository<ResultsPip,Long> {

    List<ResultsPip> findByPipId(Long pipId);

}
