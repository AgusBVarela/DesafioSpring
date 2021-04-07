package com.example.demo.repositories;

import com.example.demo.dtos.ArticleDTO;
import com.example.demo.dtos.TicketDTO;
import com.example.demo.exceptions.InvalidQuantityExceptions;
import com.example.demo.exceptions.ProductNotFoundExceptions;

import java.util.List;
import java.util.Map;

public interface CatalogueRepository {

    List<ArticleDTO> getArticlesByFilter(Map<String, String> category);


    TicketDTO buyArticles(TicketDTO buy) throws InvalidQuantityExceptions, ProductNotFoundExceptions;
}
