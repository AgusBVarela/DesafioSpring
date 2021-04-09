package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseDTO {
    private TicketDTO ticket;
    private StatusDTO statusCode;
}
