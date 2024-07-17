package com.javaguru.phonecodeservice.service;

import com.javaguru.phonecodeservice.dto.PhoneCodeDto;
import org.springframework.stereotype.Service;

@Service
public interface PhoneCodeService {
    PhoneCodeDto getCodeById(Integer id);
    PhoneCodeDto getIdByCode(Integer id);
    PhoneCodeDto save(PhoneCodeDto phoneCodeDto);
}
