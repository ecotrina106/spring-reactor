package com.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Línea para ignorar nulos en salida
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDTO {

    private String id;
    private String description;
    private ClientDTO client;
    private List<InvoiceDetailDTO> items;

}

