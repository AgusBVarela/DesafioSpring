package com.example.demo.validations;

import com.example.demo.dtos.ClientDTO;
import com.example.demo.exceptions.IncompleteDataClientException;
import com.example.demo.exceptions.InvalidFilterExceptions;
import com.example.demo.exceptions.InvalidInstanceDBException;

import java.util.Map;

public class ClientValidation {

    public static void validateParams(Map<String, String> params) throws InvalidFilterExceptions {
        /*Valida que los valores recibidos por parámetro sean válidos. En caso de no ser así lanza una excepción.*/

        if(params.containsKey("dni")){
            try{
                Long dni = Long.parseLong(params.get("dni"));
                if(params.get("dni").length() < 1 || params.get("dni").length() >8){
                    throw new InvalidFilterExceptions("El dni debe ser un numero de entre 1 y 8 digitos.");
                }
            }
            catch (Exception e){
                throw new InvalidFilterExceptions("El prestige debe ser un valor numerico.");
            }
        }
    }

    public static void validateClient(ClientDTO client) throws IncompleteDataClientException {
        /*Valida que el cliente ingresado por parámetro cumpla con todos los requisitos y atributos esperados.*/

        if(client == null || client.getName() == null || client.getName().isEmpty() ||
                client.getLastName() == null || client.getLastName().isEmpty() || client.getDni() == 0 ||
                client.getEmail() == null || client.getEmail().isEmpty() ||
                client.getProvince() == null || client.getProvince().isEmpty()) throw new IncompleteDataClientException();
    }

    public static void ValidateClientDB(String[] datos) throws InvalidInstanceDBException {
        /*Valida las instancias recibidas del archivo de DB. En caso de sus valores ser erróneos
        o poseer valores de esas condiciones, lanza una excepción.*/

        int maxParamPos = 5;
        String message = "El cliente " + datos[2] + " posee datos vacios.";
        for(int i = 0; i <= maxParamPos; i++){
            if(datos == null || datos[i] == null) throw new InvalidInstanceDBException(message);
        }

        try {
            long dni = Long.valueOf(datos[0]);
            int years = Integer.valueOf(datos[4]);

        } catch (Exception e) {
            message = "El cliente " + datos[2] + " posee datos en un formato inesperado: " + e.getMessage();
            throw new InvalidInstanceDBException(message);
        }
    }
}
