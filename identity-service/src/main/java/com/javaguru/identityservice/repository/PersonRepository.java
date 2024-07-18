package com.javaguru.identityservice.repository;

import com.javaguru.identityservice.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByNickname(String nickname);
    Optional<Person> findByEmail(String email);
    Optional<Person> findByNicknameAndPassword(String nickname, String password);
}
