package com.example.demo.services;

import com.example.demo.dtos.ClientDTO;
import com.example.demo.exceptions.ExistsClientException;
import com.example.demo.exceptions.IncompleteDataClientException;
import com.example.demo.exceptions.InvalidFilterExceptions;
import com.example.demo.repositories.ClientRepository;
import com.example.demo.validations.ClientValidation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ClientServiceImple implements ClientService {

    private ClientRepository clientRepository;

    public ClientServiceImple(ClientRepository clientRep){
        this.clientRepository = clientRep;
    }

    @Override
    public ClientDTO addClient(ClientDTO client) throws IncompleteDataClientException, IOException, ExistsClientException {
        /*Encargado de validar los datos ingresados por parámetro. En caso de ser correctos se delega su incorporación
        y almacenamiento al repositorio.*/

        ClientValidation.validateClient(client);
        return clientRepository.addClient(client);
    }

    @Override
    public List<ClientDTO> getClients(Map<String, String> filter) throws InvalidFilterExceptions {
         /*Encargado de validar los datos ingresados por parámetro. En caso de ser correctos se delega la obtención
         de los datos al repositorio, el resultado obtenido es devuelto.*/

        ClientValidation.validateParams(filter);
        List<ClientDTO> result = clientRepository.getClientsByFilter(filter);
        return result;
    }


}
