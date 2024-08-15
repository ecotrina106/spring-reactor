package com.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "dishes")
public class Dish {

    @Id
    private String id;
    @Field
    private String name;
    //si se usa el primitivo double el valor por defecto sería 0
    //Al usar la clase Double el valor por defecto es null
    @Field
    private Double price;
    @Field
    private Boolean status;
}
