package com.Telnet.AuthService.repository;



import com.Telnet.AuthService.model.ERole;
import com.Telnet.AuthService.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
Optional<Role> findByName(ERole name);
    Optional<Role> findFirstByName(ERole name);
    Optional<Role> findByNameIn(List<ERole> names);
    List<Role> findAllByNameIn(List<ERole> names); // N

}
