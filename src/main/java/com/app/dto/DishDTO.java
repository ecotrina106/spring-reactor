package com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DishDTO {

    private String id;
    private String nameDish;
    private String priceDish;
    private Boolean statusDish;

}
