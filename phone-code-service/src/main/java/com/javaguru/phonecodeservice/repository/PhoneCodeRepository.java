package com.javaguru.phonecodeservice.repository;

import com.javaguru.phonecodeservice.entity.PhoneCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface PhoneCodeRepository extends JpaRepository<PhoneCode, Integer> {
    Optional<PhoneCode> findByCode(Integer code);
    PhoneCode save(PhoneCode code);
}
