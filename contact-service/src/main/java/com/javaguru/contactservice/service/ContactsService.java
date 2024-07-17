package com.javaguru.contactservice.service;

import com.javaguru.contactservice.dto.ContactsDto;
import org.springframework.stereotype.Service;

@Service
public interface ContactsService {
    ContactsDto getContacts(Long id);
    ContactsDto saveContacts(ContactsDto contactsDto);
}
