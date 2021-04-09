package com.example.demo.controllers;
import com.example.demo.dtos.StatusDTO;
import com.example.demo.dtos.ResponseDTO;
import com.example.demo.dtos.TicketDTO;
import com.example.demo.exceptions.InvalidFilterExceptions;
import com.example.demo.exceptions.InvalidQuantityException;
import com.example.demo.exceptions.ManyFiltersException;
import com.example.demo.exceptions.ProductNotFoundException;
import com.example.demo.services.CatalogueServiceImple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CatalogueController {

    @Autowired
    private CatalogueServiceImple catalogueService;

    @GetMapping("/articles")
    public ResponseEntity getArticles(@RequestParam Map<String, String> params) throws ManyFiltersException, InvalidFilterExceptions {
        return new ResponseEntity(catalogueService.getArticles(params), HttpStatus.OK);
    }

    @PostMapping("/purchase-request")
    public ResponseEntity buyArticles(@RequestBody TicketDTO buy) throws InvalidQuantityException, ProductNotFoundException, IOException {
        ResponseDTO response = new ResponseDTO(catalogueService.buyArticles(buy), new StatusDTO("200", "La solicitud de compra se completó con éxito"));
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("/add-cart")
    public ResponseEntity addToCart(@RequestBody TicketDTO buy) throws InvalidQuantityException, ProductNotFoundException, IOException {
        return new ResponseEntity(catalogueService.addToCart(buy), HttpStatus.OK);
    }

    @GetMapping("/buy-cart")
    public ResponseDTO buyCart() throws InvalidQuantityException, IOException, ProductNotFoundException {
        ResponseDTO response = new ResponseDTO(catalogueService.buyCart(), new StatusDTO("200", "La solicitud de compra se completó con éxito"));
        return response;
    }
    @ExceptionHandler(ManyFiltersException.class )
    public ResponseEntity<StatusDTO> handleException(ManyFiltersException e) {
        StatusDTO errorDTO = new StatusDTO("Many Filters.", e.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(InvalidFilterExceptions.class )
    public ResponseEntity<StatusDTO> handleException(InvalidFilterExceptions e) {
        StatusDTO errorDTO = new StatusDTO("Invalid Filter.", e.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(InvalidQuantityException.class )
    public ResponseEntity<StatusDTO> handleException(InvalidQuantityException e) {
        StatusDTO errorDTO = new StatusDTO("Invalid Quantity.", e.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(ProductNotFoundException.class )
    public ResponseEntity<StatusDTO> handleException(ProductNotFoundException e) {
        StatusDTO errorDTO = new StatusDTO("Product not found.", e.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }
}
