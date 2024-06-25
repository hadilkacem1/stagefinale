package com.Telnet.pip.repository;

import com.Telnet.pip.model.Pip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PipRepository extends JpaRepository<Pip,Long> {
    List<Pip> findByCategoryId(Long categoryId);


    List<Pip> findByCategoryName(String categoryName);
}
