
package com.Telnet.AuthService.repository;
import com.Telnet.AuthService.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query("select t from Token t inner join t.user u where u.id = :userId and t.loggedOut = false")
    List<Token> findAllTokensByUser(Integer userId);


    Optional<Token> findByToken(String token);
}

