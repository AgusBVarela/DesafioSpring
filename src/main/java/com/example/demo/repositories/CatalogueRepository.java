package com.example.demo.repositories;

import com.example.demo.dtos.ArticleDTO;
import com.example.demo.dtos.TicketDTO;
import com.example.demo.exceptions.InvalidQuantityException;
import com.example.demo.exceptions.ProductNotFoundException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CatalogueRepository {

    List<ArticleDTO> getArticlesByFilter(Map<String, String> category);

    TicketDTO buyArticles(TicketDTO buy) throws InvalidQuantityException, ProductNotFoundException, IOException;
    String addToCart(TicketDTO buy) throws InvalidQuantityException, ProductNotFoundException;
    TicketDTO buyCart() throws InvalidQuantityException, IOException, ProductNotFoundException;
}
