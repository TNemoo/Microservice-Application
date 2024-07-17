package com.javaguru.contactservice.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactsDto implements Serializable {
        @NotBlank
        @Column(unique = true)
        Long cvsId;

        @NotNull
        @Min(value = 1, message = "Code should be greater than 1")
        @Max(value = 500, message = "Code can't be more than 500")
        Integer phoneCode;
        /** здесь уде не ссылка на другую таблицу, а сам код страны */

        @NotBlank
        @Pattern(regexp = "\\++\\d{2,}+\\-+\\d{3}+\\-+\\d{3}+\\-+\\d{2}+\\-+\\d{2}",
                message = "Format phone number: +XX-XXX-XXX-XX-XX") // 17 symbols
        @Column(unique = true)
        String phoneNumber;

        @NotBlank
        @Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
                message = "Format e-mail address: example@example.com")
        @Column(unique = true)
        String email;

        @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9.,-_]{5,31}$")
        @Column(unique = true)
        String skype; //url

        @Pattern(regexp = "^https:\\/\\/(www\\.)?linkedin\\.com\\/in\\/[a-zA-Z0-9-]{5,30}\\/?$")
        @Column(unique = true)
        String linkedin; //url

        @Pattern(regexp = "^(https?:\\/\\/)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d+)?(\\/[-a-zA-Z0-9._~:?#\\[\\]@!$&'()*+,;=%]*)?$")
        @Column(name = "portfolio_link", unique = true)
        String portfolioLink; //url


        @Override
        public String toString() {
                return "ContactsDto{" +
                        "CurriculumVitaeServiceId=" + cvsId +
                        ", phoneCodeId=" + phoneCode +
                        ", phoneNumber='" + phoneNumber + '\'' +
                        ", email='" + email + '\'' +
                        ", skype='" + skype + '\'' +
                        ", linkedin='" + linkedin + '\'' +
                        ", portfolioLink='" + portfolioLink + '\'' +
                        '}';
        }
}