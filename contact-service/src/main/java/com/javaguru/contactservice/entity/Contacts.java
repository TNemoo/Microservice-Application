package com.javaguru.contactservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Contacts {

    @Id
    @Column(name = "cv_id")
    private Long cvsId;

    @NotNull
    @Min(value = 1, message = "Code should be greater than 1")
    @Max(value = 500, message = "Code can't be more than 500")
    @Column(name = "phone_code_id")
    private Integer phoneCodeId;;

    @NotBlank
    @Pattern(regexp = "\\++\\d{2,}+\\-+\\d{3}+\\-+\\d{3}+\\-+\\d{2}+\\-+\\d{2}",
            message = "Format phone number: +XX-XXX-XXX-XX-XX") // 17 symbols
    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            message = "Format e-mail address: example@example.com")
    @Column(unique = true)
    private String email;

    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9.,-_]{5,31}$")
    @Column(unique = true)
    private String skype;  //url

    @Pattern(regexp = "^https:\\/\\/(www\\.)?linkedin\\.com\\/in\\/[a-zA-Z0-9-]{5,30}\\/?$")
    @Column(unique = true)
    private String linkedin; //url

    @Pattern(regexp = "^(https?:\\/\\/)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d+)?(\\/[-a-zA-Z0-9._~:?#\\[\\]@!$&'()*+,;=%]*)?$")
    @Column(name = "portfolio_link", unique = true)
    private String portfolioLink; //url

    @Override
    public String toString() {
        return "Contacts{" +
                "cvsId=" + cvsId +
                ", phoneCodeId=" + phoneCodeId +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", skype='" + skype + '\'' +
                ", linkedin='" + linkedin + '\'' +
                ", portfolioLink='" + portfolioLink + '\'' +
                '}';
    }
}
