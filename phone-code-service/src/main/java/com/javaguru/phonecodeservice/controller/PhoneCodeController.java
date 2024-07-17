package com.javaguru.phonecodeservice.controller;

import com.javaguru.phonecodeservice.dto.PhoneCodeDto;
import com.javaguru.phonecodeservice.service.PhoneCodeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/codes")
public class PhoneCodeController {

    private final PhoneCodeService phoneCodeService;

    @GetMapping()
    public ResponseEntity<?> getId(@RequestParam("code") @Valid Integer code) {
        PhoneCodeDto phoneCodeDto = phoneCodeService.getIdByCode(code);
        return new ResponseEntity<>(phoneCodeDto.getId(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCode(@PathVariable("id") @Valid Integer id) {
        PhoneCodeDto phoneCodeDto = phoneCodeService.getCodeById(id);
        return new ResponseEntity<>(phoneCodeDto.getCode(), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> saveCode(@RequestBody @Valid PhoneCodeDto phoneCodeDto) {
        phoneCodeDto = phoneCodeService.save(phoneCodeDto);
        return new ResponseEntity<>(phoneCodeDto, HttpStatus.NO_CONTENT);
    }
}
