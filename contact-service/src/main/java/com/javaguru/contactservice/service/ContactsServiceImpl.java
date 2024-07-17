package com.javaguru.contactservice.service;

import com.javaguru.contactservice.dto.ContactsDto;
import com.javaguru.contactservice.entity.Contacts;
import com.javaguru.contactservice.repository.ContactsRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/** Этот сервис отменен, т.к. заменен на ContactsServiceFeignClientImpl в силу замены RestTemplate на FeignClient */
@RequiredArgsConstructor
//@Service
public class ContactsServiceImpl implements ContactsService {

    private final ContactsRepository contactsRepository;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;

    public ContactsDto getContacts(Long id) {
        Optional<Contacts> contactsOpt = contactsRepository.findByCvsId(id);
        contactsOpt.orElseThrow(() -> new NotFoundException("There are not contacts"));

        Contacts contacts = contactsOpt.get();

//        Integer code = restTemplate.exchange("http://localhost:8002/codes/" + contacts.getPhoneCodeId(),
//                HttpMethod.GET, null, Integer.class).getBody().intValue();

        Integer code = restTemplate.exchange("http://PHONE-CODE-SERVICE/codes/" + contacts.getPhoneCodeId(),
                HttpMethod.GET, null, Integer.class).getBody().intValue();

        ContactsDto contactsDto = modelMapper.map(contactsOpt.get(), ContactsDto.class);
        contactsDto.setPhoneCode(code);
        return contactsDto;
    }

    public ContactsDto saveContacts(ContactsDto contactsDto) {
        Optional<Contacts> contactsExist = contactsRepository.findByPhoneNumber(contactsDto.getPhoneNumber());
        contactsExist.ifPresent(v -> new BadRequestException("That phone number is already exist"));

        Contacts contacts = modelMapper.map(contactsDto, Contacts.class);

//        Integer codeId = restTemplate.exchange("http://localhost:8002/codes?code=" + contactsDto.getPhoneCode(),
//                HttpMethod.GET, null, Integer.class).getBody().intValue();

        Integer codeId = restTemplate.exchange("http://PHONE-CODE-SERVICE/codes?code=" + contactsDto.getPhoneCode(),
                HttpMethod.GET, null, Integer.class).getBody().intValue();

        contacts.setPhoneCodeId(codeId);

        return modelMapper.map(contactsRepository.save(contacts), ContactsDto.class);
    }
}
