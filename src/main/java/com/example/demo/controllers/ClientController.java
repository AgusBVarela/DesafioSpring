package com.example.demo.controllers;

import com.example.demo.dtos.ClientDTO;
import com.example.demo.dtos.StatusDTO;
import com.example.demo.exceptions.*;
import com.example.demo.services.ClientServiceImple;
import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ClientController {

    @Autowired
    private ClientServiceImple clientService;

    @PostMapping("/add-client")
    public ResponseEntity addClient(@RequestBody ClientDTO client) throws IncompleteDataClientException, IOException, ExistsClientException {
        return new ResponseEntity(clientService.addClient(client), HttpStatus.OK);
    }

    @GetMapping("/clients")
    public ResponseEntity getClients(@RequestParam Map<String, String> params) throws InvalidFilterExceptions {
        return new ResponseEntity(clientService.getClients(params), HttpStatus.OK);
    }
    
    @ExceptionHandler(IncompleteDataClientException.class )
    public ResponseEntity<StatusDTO> handleException(IncompleteDataClientException e) {
        StatusDTO errorDTO = new StatusDTO("Incomplete Data.", e.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }
    @ExceptionHandler(ExistsClientException.class )
    public ResponseEntity<StatusDTO> handleException(ExistsClientException e) {
        StatusDTO errorDTO = new StatusDTO("Exists Client.", e.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }
    @ExceptionHandler(InvalidInstanceDBException.class )
    public ResponseEntity<StatusDTO> handleException(InvalidInstanceDBException e) {
        StatusDTO errorDTO = new StatusDTO("Exists Client.", e.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }
}
