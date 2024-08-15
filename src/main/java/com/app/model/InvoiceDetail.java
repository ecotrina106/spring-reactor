package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//OJO, clase de apoyo para estructurar invoice, pero no es necesario una factura por ser NOSQL
public class InvoiceDetail {

    private int quantity;
    private Dish dish;

}
