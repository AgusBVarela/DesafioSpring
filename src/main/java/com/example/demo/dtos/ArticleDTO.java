package com.example.demo.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArticleDTO {
    private Integer productId = null;
    private String name = null;
    private String brand = null;
    private Integer quantity = null;
    private String category = null;
    private Double price = null;
    private Boolean freeShipping = null;
    private Integer prestige = null;
}
