package com.javaguru.contactservice.service;

import com.javaguru.contactservice.dto.ContactsDto;
import com.javaguru.contactservice.entity.Contacts;
import com.javaguru.contactservice.repository.ContactsRepository;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ContactsServiceFeignClientImpl implements ContactsService {

    private final ContactsRepository contactsRepository;
    private final ModelMapper modelMapper;
    private final PhoneCodeServiceClient phoneCodeServiceClient;

    public ContactsDto getContacts(Long id) {
        Optional<Contacts> contactsOpt = contactsRepository.findByCvsId(id);
        contactsOpt.orElseThrow(() -> new NotFoundException("There are not contacts"));

        Contacts contacts = contactsOpt.get();

        // Feign Client вместо RestTemplate:
        Integer code = phoneCodeServiceClient.getCodeById(contacts.getPhoneCodeId());

        ContactsDto contactsDto = modelMapper.map(contactsOpt.get(), ContactsDto.class);
        contactsDto.setPhoneCode(code);
        return contactsDto;
    }

    public ContactsDto saveContacts(ContactsDto contactsDto) {
        Optional<Contacts> contactsExist = contactsRepository.findByPhoneNumber(contactsDto.getPhoneNumber());
        contactsExist.ifPresent(v -> new BadRequestException("That phone number is already exist"));

        Contacts contacts = modelMapper.map(contactsDto, Contacts.class);

        // Feign Client вместо RestTemplate:
        Integer codeId = phoneCodeServiceClient.getIdByCode(contactsDto.getPhoneCode());

        contacts.setPhoneCodeId(codeId);

        return modelMapper.map(contactsRepository.save(contacts), ContactsDto.class);
    }
}
