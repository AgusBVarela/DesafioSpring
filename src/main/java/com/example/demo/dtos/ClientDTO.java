package com.example.demo.dtos;

import lombok.Data;

@Data
public class ClientDTO {
    private long dni;
    private String name;
    private String lastName;
    private String email;
    private int years;
    private String province;
}
