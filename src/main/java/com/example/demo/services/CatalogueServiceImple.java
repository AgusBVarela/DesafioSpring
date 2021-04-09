package com.example.demo.services;

import com.example.demo.dtos.ArticleDTO;
import com.example.demo.dtos.TicketDTO;
import com.example.demo.exceptions.InvalidFilterExceptions;
import com.example.demo.exceptions.InvalidQuantityException;
import com.example.demo.exceptions.ManyFiltersException;
import com.example.demo.exceptions.ProductNotFoundException;
import com.example.demo.repositories.CatalogueRepository;
import com.example.demo.sorters.Sort;
import com.example.demo.validations.CatalogueValidation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CatalogueServiceImple implements CatalogueService{

    private CatalogueRepository catalogueRepository;

    public CatalogueServiceImple(CatalogueRepository catalogueRepository){
        this.catalogueRepository = catalogueRepository;
    }


    @Override
    public List<ArticleDTO> getArticles(Map<String, String> filter) throws ManyFiltersException, InvalidFilterExceptions {
        /*Encargado de devolver una lista con los articulos con las condiciones recibidas por parámetros:
        filtrados según corresponda y/o en orden elegido.
          Valida que los filtros sean válidos y de no ser así lanza una excepción. Delega la búsqueda de artículos
          al repositorio para solictarlos a la base de datos.*/

        if((filter.size() == 3 && !filter.containsKey("order")) || filter.size() > 3) throw new ManyFiltersException();

        CatalogueValidation.ValidateParams(filter);
        List<ArticleDTO> result = catalogueRepository.getArticlesByFilter(filter);
        if(filter.containsKey("order")) Sort.SortBy(filter.get("order"), result);

        return result;
    }

    @Override
    public TicketDTO buyArticles(TicketDTO buy) throws InvalidQuantityException, ProductNotFoundException, IOException {
        /*Encargado de delegar la compra de artículos al repositorio.*/

        return catalogueRepository.buyArticles(buy);
    }

    @Override
    public TicketDTO buyCart() throws InvalidQuantityException, IOException, ProductNotFoundException {
        /*Encargado de delegar la compra del carrito al repositorio.*/

        return catalogueRepository.buyCart();
    }

    @Override
    public String addToCart(TicketDTO buy) throws InvalidQuantityException, ProductNotFoundException {
        /*Le delega al repositorio almacenar el carrito con las posibles compras del usuario.
        Devuelve el total acmumulado.*/

        return catalogueRepository.addToCart(buy);
    }
}
