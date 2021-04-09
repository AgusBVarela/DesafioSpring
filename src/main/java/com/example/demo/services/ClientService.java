package com.example.demo.services;

import com.example.demo.dtos.ClientDTO;
import com.example.demo.exceptions.ExistsClientException;
import com.example.demo.exceptions.IncompleteDataClientException;
import com.example.demo.exceptions.InvalidFilterExceptions;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ClientService {

    ClientDTO addClient(ClientDTO client) throws IncompleteDataClientException, IOException, ExistsClientException;

    List<ClientDTO> getClients(Map<String, String> params) throws InvalidFilterExceptions;
}
