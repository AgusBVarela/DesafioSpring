package com.example.demo.repositories;

import com.example.demo.dtos.ArticleDTO;
import com.example.demo.dtos.TicketDTO;
import com.example.demo.exceptions.InvalidQuantityExceptions;
import com.example.demo.exceptions.ProductNotFoundExceptions;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class CatalogueRepositoryImple implements CatalogueRepository {
    private List<ArticleDTO> products = null;
    private Map<Integer, ArticleDTO> productsMap = null;
    private AtomicLong ticketId = new AtomicLong(1);

    private void loadDataBase() {
        products = new ArrayList<ArticleDTO>();
        productsMap = new HashMap<>();
        String csvFile = "src/main/resources/dbProductos.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            br.readLine(); // elimino la cabecera
            while ((line = br.readLine()) != null) {
                String[] datos = line.split(cvsSplitBy);

                ArticleDTO product =  new ArticleDTO();
                int productId = Integer.valueOf(datos[0]);
                product.setProductId(productId);
                product.setName(datos[1]);
                product.setCategory(datos[2]);
                product.setBrand(datos[3]);
                double price = Double.valueOf(datos[4].replace("$", "").replace(".", ""));
                product.setPrice(price);
                int quantity = Integer.valueOf(datos[5]);
                product.setQuantity(quantity);
                Boolean freeShipping = datos[6].toUpperCase() == "SI" ? true : false;
                product.setFreeShipping(freeShipping);
                int prestige = StringUtils.countOccurrencesOf(datos[7], "*");
                product.setPrestige(prestige);

                products.add(product);
                productsMap.put(productId,product);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void saveDataBase(List<ArticleDTO> articles) throws IOException {
        String csvFile = "src/main/resources/dbProductos.csv";
        FileWriter writer = new FileWriter(csvFile);

        String collect = "name;category;brand;price;quantity;freeShipping;prestige\n";
        for(ArticleDTO article: articles) {
            collect += article.getName() + "," + article.getCategory() + "," + article.getBrand() + "," + article.getPrice() + "," +
                    article.getQuantity() + "," + article.getFreeShipping() + "," + "*****"+  "\n";

        }

        writer.write(collect);
        writer.close();
    }

    private void ValidateHaveProducts(){
        if(products == null) this.loadDataBase();
    }

    @Override
    public List<ArticleDTO> getArticlesByFilter(Map<String, String> filters) {
        this.ValidateHaveProducts();

        Map<Integer, ArticleDTO> m = this.productsMap.entrySet().stream().filter(
                prod-> ((filters.get("category") == null || prod.getValue().getCategory().toUpperCase().equals(filters.get("category").toUpperCase())) &&
                        (filters.get("product") == null || prod.getValue().getName().toUpperCase().equals(filters.get("product").toUpperCase())) &&
                        (filters.get("brand") == null || prod.getValue().getBrand().toUpperCase().equals(filters.get("brand").toUpperCase())) &&
                        (filters.get("price") == null || Double.compare(prod.getValue().getPrice(), Double.valueOf(filters.get("price"))) == 0) &&
                        (filters.get("freeShipping") == null || (Boolean.compare(prod.getValue().getFreeShipping(), Boolean.valueOf(filters.get("freeShipping"))) == 0) &&
                                (filters.get("prestige") == null || (Integer.compare(prod.getValue().getPrestige(), Integer.valueOf(filters.get("prestige")))) == 0 )))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new ArrayList<ArticleDTO>(m.values());
        /*List<ArticleDTO> list = new ArrayList<ArticleDTO>(m.values());

        List<ArticleDTO> nn =  this.products.stream()
                .filter(prod -> ((filters.get("category") == null || prod.getCategory().toUpperCase().equals(filters.get("category").toUpperCase())) &&
                                (filters.get("product") == null || prod.getName().toUpperCase().equals(filters.get("product").toUpperCase())) &&
                                (filters.get("brand") == null || prod.getBrand().toUpperCase().equals(filters.get("brand").toUpperCase())) &&
                                (filters.get("price") == null || Double.compare(prod.getPrice(), Double.valueOf(filters.get("price"))) == 0) &&
                                (filters.get("freeShipping") == null || (Boolean.compare(prod.getFreeShipping(), Boolean.valueOf(filters.get("freeShipping"))) == 0) &&
                                (filters.get("prestige") == null || (Integer.compare(prod.getPrestige(), Integer.valueOf(filters.get("prestige")))) == 0 ))))
                .collect(Collectors.toList());
        return list;*/
    }

    @Override
    public TicketDTO buyArticles(TicketDTO buy) throws InvalidQuantityExceptions, ProductNotFoundExceptions {
        double total = 0;
        this.ValidateHaveProducts();

        List<ArticleDTO> purchased = new ArrayList<>();
        for(ArticleDTO article : buy.getArticles()){
            int result = 0;
            if((result = BuyArticle(article.getProductId(), article.getQuantity())) == 0){
                purchased.add(article);
                total += (article.getQuantity() * productsMap.get(article.getProductId()).getPrice());
            }
            else{
                this.UpdateProductsInvalidBuy(purchased);
                String id = String.valueOf(article.getProductId());
                if(result == -1){
                    throw new ProductNotFoundExceptions("El producto "+ id + " no existe.");
                }else{
                    throw new InvalidQuantityExceptions("El producto "+ id + " no posee la cantidad solicitada.");
                }
             }
        }
        //Update
        return new TicketDTO(ticketId.getAndIncrement(), purchased, total);

    }

    private int BuyArticle(int productId, int quantity) {
        ArticleDTO article = productsMap.get(productId);
        if(article == null) return -1;
        if(quantity > article.getQuantity()) return quantity;
        return 0;
    }

    private void UpdateProductsInvalidBuy(List<ArticleDTO> articlesToUpdate){
        /*Actualizo la db, retorno las modificaciones hechas ya que no se realizo la compra*/
        for(ArticleDTO articleUpdate : articlesToUpdate){
           ArticleDTO oldArticle = productsMap.get(articleUpdate.getProductId());
           oldArticle.setQuantity(oldArticle.getQuantity() + articleUpdate.getQuantity());
        }
    }


}
