package com.javaguru.identityservice.repository;

import com.javaguru.identityservice.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByNickname(String nickname);
    Optional<Person> findByEmail(String email);
}
