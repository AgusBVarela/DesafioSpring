package com.example.demo.services;

import com.example.demo.dtos.ArticleDTO;
import com.example.demo.dtos.ResponseDTO;
import com.example.demo.dtos.TicketDTO;
import com.example.demo.exceptions.InvalidFilterExceptions;
import com.example.demo.exceptions.InvalidQuantityExceptions;
import com.example.demo.exceptions.ManyFiltersExceptions;
import com.example.demo.exceptions.ProductNotFoundExceptions;

import java.util.List;
import java.util.Map;

public interface CatalogueService {

    List<ArticleDTO> getArticles(Map<String, String> params) throws ManyFiltersExceptions, InvalidFilterExceptions;
    TicketDTO buyArticles(TicketDTO buy) throws InvalidQuantityExceptions, ProductNotFoundExceptions;

}
