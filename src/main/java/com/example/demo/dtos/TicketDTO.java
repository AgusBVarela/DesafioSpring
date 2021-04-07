package com.example.demo.dtos;

import lombok.Data;
import java.util.List;

@Data
public class TicketDTO {
    private long id;
    private List<ArticleDTO> articles;
    private double total;

    public TicketDTO(List<ArticleDTO> articles) {
        this.articles = articles;
    }

    public TicketDTO(long id, List<ArticleDTO> articles, double total){
        this.id = id;
        this.total = total;
        this.articles = articles;
    }
}
