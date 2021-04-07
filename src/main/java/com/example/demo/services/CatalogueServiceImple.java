package com.example.demo.services;

import com.example.demo.dtos.ArticleDTO;
import com.example.demo.dtos.ErrorDTO;
import com.example.demo.dtos.ResponseDTO;
import com.example.demo.dtos.TicketDTO;
import com.example.demo.exceptions.InvalidFilterExceptions;
import com.example.demo.exceptions.InvalidQuantityExceptions;
import com.example.demo.exceptions.ManyFiltersExceptions;
import com.example.demo.exceptions.ProductNotFoundExceptions;
import com.example.demo.repositories.CatalogueRepository;
import com.example.demo.sorters.Sort;
import com.sun.net.httpserver.HttpsServer;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CatalogueServiceImple implements CatalogueService{

    private CatalogueRepository catalogueRepository;

    public CatalogueServiceImple(CatalogueRepository catalogueRepository){
        this.catalogueRepository = catalogueRepository;
    }


    @Override
    public List<ArticleDTO> getArticles(Map<String, String> params) throws ManyFiltersExceptions, InvalidFilterExceptions {
        if((params.size() == 3 && !params.containsKey("order")) || params.size() > 3) throw new ManyFiltersExceptions();

        this.ValidateParams(params);
        List<ArticleDTO> result = catalogueRepository.getArticlesByFilter(params);
        if(params.containsKey("order")) Sort.SortBy(params.get("order"), result);

        return result;
    }

    @Override
    public TicketDTO buyArticles(TicketDTO buy) throws InvalidQuantityExceptions, ProductNotFoundExceptions {
        return catalogueRepository.buyArticles(buy);
    }

    //Todo alguno que no pertenexca a la lista
    private void ValidateParams(Map<String, String> params) throws InvalidFilterExceptions {
        if(params.containsKey("order")){
            try{
                int num = Integer.parseInt(params.get("order"));
                if (num < 0 || num >3) throw new InvalidFilterExceptions("El orden debe ser entre 0 y 3.");
            }
            catch (Exception e){
                throw new InvalidFilterExceptions("El order no es de valor numérico");
            }
        }
        if(params.containsKey("freeShipping")){
            try{
                Boolean.parseBoolean(params.get("freeShipping"));
            }
            catch (Exception e){
                throw new InvalidFilterExceptions("El freeShipping debe ser 'true' o 'false'.");
            }
        }
        //ToDo: cambiar el prestigio a entero pa devolver en string
        if(params.containsKey("prestige")){
            try{
                Integer.parseInt(params.get("prestige"));
            }
            catch (Exception e){
                throw new InvalidFilterExceptions("El prestige debe ser un valor numerico.");
            }
        }
    }


}
