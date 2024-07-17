package com.javaguru.contactservice.repository;

import com.javaguru.contactservice.entity.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface ContactsRepository extends JpaRepository<Contacts, Long> {
    Optional<Contacts> findByCvsId(Long id);
    Contacts save(Contacts contacts);
    Optional<Contacts> findByPhoneNumber(String phoneNumber);
}
