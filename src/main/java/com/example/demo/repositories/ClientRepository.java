package com.example.demo.repositories;

import com.example.demo.dtos.ClientDTO;
import com.example.demo.exceptions.ExistsClientException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ClientRepository{
    ClientDTO addClient(ClientDTO client) throws IOException, ExistsClientException;

    List<ClientDTO> getClientsByFilter(Map<String, String> params);
}
