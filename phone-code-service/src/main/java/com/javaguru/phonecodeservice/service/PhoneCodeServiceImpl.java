package com.javaguru.phonecodeservice.service;

import com.javaguru.phonecodeservice.dto.PhoneCodeDto;
import com.javaguru.phonecodeservice.entity.PhoneCode;
import com.javaguru.phonecodeservice.repository.PhoneCodeRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.Optional;


@RequiredArgsConstructor
@Service

public class PhoneCodeServiceImpl implements PhoneCodeService {

    private final PhoneCodeRepository phoneCodeRepository;
    private final ModelMapper modelMapper;

    public PhoneCodeDto getCodeById(Integer id) {
        Optional<PhoneCode> phoneCodeOpt = phoneCodeRepository.findById(id);
        phoneCodeOpt.orElseThrow(() -> new NotFoundException("There are not code"));
        return modelMapper.map(phoneCodeOpt.get(), PhoneCodeDto.class);
    }

    public PhoneCodeDto getIdByCode(Integer code) {
        Optional<PhoneCode> phoneCodeOpt = phoneCodeRepository.findByCode(code);
        phoneCodeOpt.orElseThrow(() -> new NotFoundException("There are not code"));
        return modelMapper.map(phoneCodeOpt.get(), PhoneCodeDto.class);
    }



    public PhoneCodeDto save(PhoneCodeDto phoneCodeDto) {
        Optional<PhoneCode> phoneCodeExist = phoneCodeRepository.findById(phoneCodeDto.getId());
        phoneCodeExist.ifPresent(v -> new BadRequestException("That phone code is already exist"));
        PhoneCode phoneCode = modelMapper.map(phoneCodeDto, PhoneCode.class);
        return modelMapper.map(phoneCodeRepository.save(phoneCode), PhoneCodeDto.class);
    }
}
