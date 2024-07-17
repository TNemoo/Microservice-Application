package com.javaguru.contactservice.controller;

import com.javaguru.contactservice.dto.ContactsDto;
import com.javaguru.contactservice.service.ContactsService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/contacts")
public class ContactsController {

    private final ContactsService contactsService;

    @GetMapping
    public ResponseEntity<?> getContacts(@RequestParam("cvsId") @Valid Long id){
        ContactsDto contactsDto = contactsService.getContacts(id);
//        System.out.println(contactsDto);
        return new ResponseEntity<>(contactsDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> saveContacts(@RequestBody @Valid ContactsDto contactsDto) {
//        System.out.println(contactsDto);
        contactsDto = contactsService.saveContacts(contactsDto);
        return new ResponseEntity<>(contactsDto, HttpStatus.NO_CONTENT);
    }

}
