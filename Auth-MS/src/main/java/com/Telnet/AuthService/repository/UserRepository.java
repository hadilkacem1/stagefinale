package com.Telnet.AuthService.repository;

import com.Telnet.AuthService.model.Role;
import com.Telnet.AuthService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAll();
    Optional<User> findByUsername(String username);
   // User findById(long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    // Ajoutez cette méthode pour rechercher un utilisateur par e-mail
    Optional<User> findByEmail(String email);


   // List<User> findByRole_Id(Long roleId);


    // Ajoutez une méthode pour récupérer le rôle par l'ID de l'utilisateur
    Optional<Role> findRoleById(Long id);
}
