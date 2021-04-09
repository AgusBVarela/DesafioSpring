package com.example.demo.repositories;

import com.example.demo.dtos.ArticleDTO;
import com.example.demo.dtos.TicketDTO;
import com.example.demo.exceptions.InvalidInstanceDBException;
import com.example.demo.exceptions.InvalidQuantityException;
import com.example.demo.exceptions.ProductNotFoundException;
import com.example.demo.validations.CatalogueValidation;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class CatalogueRepositoryImple implements CatalogueRepository {
    private Map<Integer, ArticleDTO> productsMap = null;
    private AtomicLong ticketId;
    private Properties properties;
    private Map<Integer, ArticleDTO> cart;

    public CatalogueRepositoryImple() throws IOException {
        this.properties= new Properties();
        properties.load(new FileInputStream(new File("src/main/resources/application.properties")));
        loadDataBase();
        this.ticketId = new AtomicLong(1);
        this.cart = new HashMap<>();
    }

    private void loadDataBase() {
        /*Almacena los datos de la DB en memoria. Crea un HashMap donde la clave es el
        id del articulo, y el value es el artículo.*/

        productsMap = new HashMap<>();
        String csvFile = this.properties.getProperty("dbProductsFile");
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(csvFile));
            br.readLine(); // elimino la cabecera
            while ((line = br.readLine()) != null) {
                String[] datos = line.split(cvsSplitBy);
                CatalogueValidation.ValidateArticleDB(datos);

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
                Boolean freeShipping = datos[6].toUpperCase().equals("SI") ? true : false;
                product.setFreeShipping(freeShipping);
                int prestige = StringUtils.countOccurrencesOf(datos[7], "*");
                product.setPrestige(prestige);

                productsMap.put(productId,product);
            }
        } catch (FileNotFoundException | InvalidInstanceDBException e ) {
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

    private void saveDataBase() throws IOException {
        /*Encargado de 'actualizar' la DB. Almacena los datos guardados
        en memoria en el archivo correspondiente.*/

        String csvFile = this.properties.getProperty("dbProductsFile");
        FileWriter writer = new FileWriter(csvFile);

        String collect = this.properties.getProperty("initialArticleCollect") + "\n";
        for(ArticleDTO article: productsMap.values()){
            Integer intPrice = article.getPrice().intValue();
            String price = "$" + String.valueOf(intPrice);
            String freeShipping = article.getFreeShipping() ? "SI" : "NO";
            collect += article.getProductId() + "," + article.getName() + "," + article.getCategory() + "," + article.getBrand() + "," + price + "," +
                    article.getQuantity() + "," + freeShipping + "," + "*".repeat(article.getPrestige()) +  "\n";

        }

        writer.write(collect);
        writer.close();
    }

    @Override
    public List<ArticleDTO> getArticlesByFilter(Map<String, String> filters) {
        /*Encargada de devolver una lista de articulos que cumplan con el filtro enviado por parámetro. */

        Map<Integer, ArticleDTO> m = this.productsMap.entrySet().stream().filter(
                prod-> ((filters.get("category") == null || prod.getValue().getCategory().toUpperCase().equals(filters.get("category").toUpperCase())) &&
                        (filters.get("product") == null || prod.getValue().getName().toUpperCase().equals(filters.get("product").toUpperCase())) &&
                        (filters.get("brand") == null || prod.getValue().getBrand().toUpperCase().equals(filters.get("brand").toUpperCase())) &&
                        (filters.get("price") == null || Double.compare(prod.getValue().getPrice(), Double.valueOf(filters.get("price"))) == 0) &&
                        (filters.get("freeShipping") == null || (Boolean.compare(prod.getValue().getFreeShipping(), Boolean.valueOf(filters.get("freeShipping"))) == 0) &&
                                (filters.get("prestige") == null || (Integer.compare(prod.getValue().getPrestige(), Integer.valueOf(filters.get("prestige")))) == 0 )))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new ArrayList<ArticleDTO>(m.values());
    }

    @Override
    public TicketDTO buyArticles(TicketDTO buy) throws InvalidQuantityException, ProductNotFoundException, IOException {
        /*Encargado de comprobar que se puedan comprar los articulos del ticket.
        En caso de ser posible, almacena la DB llevando a cabo la compra.*/

        int total = 0;
        List<ArticleDTO> purchased = new ArrayList<>();
        for(ArticleDTO article : buy.getArticles()){
            canBuyArticle(article.getProductId(), article.getQuantity());
            total += (article.getQuantity() * productsMap.get(article.getProductId()).getPrice().intValue());
            purchased.add(article);
        }
        this.UpdateDataBase(purchased);
        return new TicketDTO(ticketId.getAndIncrement(), purchased, total);
    }

    @Override
    public String addToCart(TicketDTO buy) throws InvalidQuantityException, ProductNotFoundException {
        /*Se agrega al carrito los articulos solicitados.
        Se devuelve la suma total a pagar por de realizar la compra.*/

        String message = "";
        for(ArticleDTO art: buy.getArticles()){

            int oldQuantity = cart.get(art.getProductId()) == null ? 0 : cart.get(art.getProductId()).getQuantity();
            canBuyArticle(art.getProductId(), oldQuantity + art.getQuantity());
        }
        for(ArticleDTO art: buy.getArticles()){
            int oldQuantity = cart.get(art.getProductId()) == null ? 0 : cart.get(art.getProductId()).getQuantity();
            art.IncreaceQuantity(oldQuantity);
            cart.put(art.getProductId(), art);
        }
        return "$" + this.getTotalCart();
    }

    @Override
    public TicketDTO buyCart() throws InvalidQuantityException, IOException, ProductNotFoundException {
        /*Lleva a cabo la compra de los productos del carrito, el mismo queda vacio.*/

        TicketDTO ticket = new TicketDTO(cart.values().stream().collect(Collectors.toList()));
        TicketDTO resultTicket =  this.buyArticles(ticket);
        cart = new HashMap<>();
        return resultTicket;
    }

    private int getTotalCart(){
        /*Devuelve la suma total del precio a pagar por lo cargado en el carrito.*/

        int total = 0;
        for(ArticleDTO art: cart.values()){
            int price = productsMap.get(art.getProductId()).getPrice().intValue();
            total += (price * art.getQuantity());
        }
        return total;
    }
    private void UpdateDataBase(List<ArticleDTO> purchased) throws IOException {
        /*Encargada de actualizar los datos en memoria de la compra. Y de actualizar
        el archivo de  DB con la misma. */

        this.UpdateQuantityDataBase(purchased);
        this.saveDataBase();
    }

    private boolean canBuyArticle(int productId, int quantity) throws ProductNotFoundException, InvalidQuantityException {
        /*Encargado de validar la existencia del producto a comprar y validar que la cantidad solicitada sea
         menor a la almacenada.*/

        String id = String.valueOf(productId);

        if(!productsMap.containsKey(productId)) throw new ProductNotFoundException("El producto '"+ id + "' no existe.");
        ArticleDTO article = productsMap.get(productId);

        if(article == null ) throw new ProductNotFoundException("El producto '"+ id + "' no existe.");
        if(quantity > article.getQuantity()) throw new InvalidQuantityException("El producto '"+ id + "' no posee la cantidad solicitada.");

        return true;
    }

    private void UpdateQuantityDataBase(List<ArticleDTO> articles){
        /*Encargado de disminuir la cantidad de los articulos almacenados en memoria
        con el valor recibido por parámetro.*/

        for(ArticleDTO articleUpdate : articles){
            this.DecreaceQuantityArticleDataBase(articleUpdate);
         }
    }

    private void DecreaceQuantityArticleDataBase(ArticleDTO article){
        /*Encargado de decrementar la cantidad del artículo recibido por parametro
        en el stock almacenado por memoria.*/

        ArticleDTO oldArticle = productsMap.get(article.getProductId());
        oldArticle.DecreaceQuantity(article.getQuantity());
    }

}
