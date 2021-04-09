package com.example.demo.sorters;

import com.example.demo.dtos.ArticleDTO;

import java.lang.management.GarbageCollectorMXBean;
import java.util.Comparator;
import java.util.List;

public class  Sort {

    public static void SortBy(String order, List<ArticleDTO> articles){
        /*Encargado de ordenar con el criterio recibido por parámetro*/

        Comparator<ArticleDTO> comparator = null;
        switch (order)
        {
            case "0": //(a,b) -> si a<=b devuelve 1, sino -1
                comparator = Comparator.comparing(ArticleDTO::getName);
                break;
            case "1": //(a,b) -> si a>b devuelve 1, sino -1
                comparator = (article1, article2) -> article1.getName().compareTo(article2.getName()) == 1 ? -1 : 1;
                break;
            case "2": //(a,b) -> si a<b devuelve 1, sino -1
                comparator = (article1, article2) -> (article1.getPrice() < article2.getPrice()) ? 1 : -1;
                break;
            case "3": //(a,b) -> si a>=b devuelve 1, sino -1
                comparator = Comparator.comparing(ArticleDTO::getPrice);
                break;
            default:
                break;
        }

        articles.sort(comparator);

    }
}
