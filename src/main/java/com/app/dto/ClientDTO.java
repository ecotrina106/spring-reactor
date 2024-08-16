package com.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Línea para ignorar nulos en salida
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClientDTO {

        private String id;
        private String name;
        private String surname;
        private LocalDate birthDateClient;
        private String picture;
}
