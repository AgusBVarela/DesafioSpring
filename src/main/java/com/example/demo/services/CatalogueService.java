package com.example.demo.services;

import com.example.demo.dtos.ArticleDTO;
import com.example.demo.dtos.TicketDTO;
import com.example.demo.exceptions.InvalidFilterExceptions;
import com.example.demo.exceptions.InvalidQuantityException;
import com.example.demo.exceptions.ManyFiltersException;
import com.example.demo.exceptions.ProductNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CatalogueService {

    List<ArticleDTO> getArticles(Map<String, String> params) throws ManyFiltersException, InvalidFilterExceptions;
    TicketDTO buyArticles(TicketDTO buy) throws InvalidQuantityException, ProductNotFoundException, IOException;
    TicketDTO buyCart() throws InvalidQuantityException, IOException, ProductNotFoundException;
    String addToCart(TicketDTO buy) throws InvalidQuantityException, ProductNotFoundException;
 }
