package com.example.demo.validations;

import com.example.demo.exceptions.InvalidFilterExceptions;
import com.example.demo.exceptions.InvalidInstanceDBException;
import org.springframework.util.StringUtils;

import java.util.Map;

public class CatalogueValidation {


    public static void ValidateParams(Map<String, String> params) throws InvalidFilterExceptions {
        /*Valida que los valores a utilizar de filtro sean válidos, en caso de no serlo lanza una excepción.
        En caso de recibir una clave que no sea parte de las propiedades, no se tiene en cuenta.*/

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
        if(params.containsKey("prestige")){
            try{
                Integer.parseInt(params.get("prestige"));
            }
            catch (Exception e){
                throw new InvalidFilterExceptions("El prestige debe ser un valor numerico.");
            }
        }
    }

    public static void ValidateArticleDB(String[] datos) throws InvalidInstanceDBException {
        /*Valida las instancias recibidas del archivo de DB. En caso de sus valores ser erróneos
        o poseer valores en esas condiciones, lanza una excepción.*/

        int maxParamPos = 7;
        String message = "El artículo " + datos[0] + " posee datos vacios.";
        for(int i = 0; i <= maxParamPos; i++){
            if(datos == null || datos[i] == null) throw new InvalidInstanceDBException(message);
        }

        try {
            int productId = Integer.valueOf(datos[0]);
            double price = Double.valueOf(datos[4].replace("$", "").replace(".", ""));
            int quantity = Integer.valueOf(datos[5]);
            Boolean freeShipping = datos[6].toUpperCase() == "SI" ? true : false;
            int prestige = StringUtils.countOccurrencesOf(datos[7], "*");

        } catch (Exception e) {
            message = "El artículo " + datos[0] + " posee datos en un formato inesperado: " + e.getMessage();
            throw new InvalidInstanceDBException(message);
        }
    }
}
