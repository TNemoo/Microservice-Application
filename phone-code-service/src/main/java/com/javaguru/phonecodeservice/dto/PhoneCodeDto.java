package com.javaguru.phonecodeservice.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneCodeDto implements Serializable {
        @Id
        private Integer id;

        @NotNull
        @Min(value = 1, message = "Code should be greater than 1")
        @Max(value = 500, message = "Code can't be more than 500")
        private Integer code;

        @NotNull
        @Min(value = 1, message = "Code should be greater than 1")
        @Max(value = 500, message = "Code can't be more than 500")
        @Column(name = "country_id")
        private Integer countryId;

        @Override
        public String toString() {
                return "PhoneCode{" +
                        "id=" + id +
                        ", code=" + code +
                        ", country_id=" + countryId +
                        '}';
        }
}