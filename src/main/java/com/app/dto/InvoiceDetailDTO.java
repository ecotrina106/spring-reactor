package com.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//Línea para ignorar nulos en salida
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvoiceDetailDTO {

    private int quantity;
    private DishDTO dish;
}
