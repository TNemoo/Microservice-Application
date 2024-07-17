package com.javaguru.phonecodeservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class PhoneCode {

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
