package com.example.demo.controllers;
import com.example.demo.dtos.ErrorDTO;
import com.example.demo.dtos.ResponseDTO;
import com.example.demo.dtos.TicketDTO;
import com.example.demo.exceptions.InvalidFilterExceptions;
import com.example.demo.exceptions.InvalidQuantityExceptions;
import com.example.demo.exceptions.ManyFiltersExceptions;
import com.example.demo.exceptions.ProductNotFoundExceptions;
import com.example.demo.services.CatalogueServiceImple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class VentaController {

    @Autowired
    private CatalogueServiceImple catalogueService;

    @GetMapping("/articles")
    public ResponseEntity getArticles(@RequestParam Map<String, String> params) throws ManyFiltersExceptions, InvalidFilterExceptions {
        return new ResponseEntity(catalogueService.getArticles(params), HttpStatus.OK);
    }

    @PostMapping("/purchase-request")
    public ResponseEntity buyArticles(@RequestBody TicketDTO buy) throws InvalidQuantityExceptions, ProductNotFoundExceptions {
        ResponseDTO response = new ResponseDTO(catalogueService.buyArticles(buy), new ErrorDTO("200", "OK"));
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @ExceptionHandler(ManyFiltersExceptions.class )
    public ResponseEntity<ErrorDTO> handleException(ManyFiltersExceptions e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid Operation.", e.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(InvalidFilterExceptions.class )
    public ResponseEntity<ErrorDTO> handleException(InvalidFilterExceptions e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid Filter.", e.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(InvalidFilterExceptions.class )
    public ResponseEntity<ErrorDTO> handleException(InvalidQuantityExceptions e) {
        ErrorDTO errorDTO = new ErrorDTO("Invalid Quantity.", e.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(ProductNotFoundExceptions.class )
    public ResponseEntity<ErrorDTO> handleException(ProductNotFoundExceptions e) {
        ErrorDTO errorDTO = new ErrorDTO("Product not found.", e.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }
}
