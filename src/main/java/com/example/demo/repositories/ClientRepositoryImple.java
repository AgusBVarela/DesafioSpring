package com.example.demo.repositories;

import com.example.demo.dtos.ClientDTO;
import com.example.demo.exceptions.ExistsClientException;
import com.example.demo.exceptions.InvalidInstanceDBException;
import com.example.demo.validations.ClientValidation;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Repository
public class ClientRepositoryImple implements ClientRepository {

    List<ClientDTO> clients = null;
    private Properties properties;

    public ClientRepositoryImple() throws IOException {
        this.properties= new Properties();
        properties.load(new FileInputStream(new File("src/main/resources/application.properties")));
        loadClientsDataBase();
    }

    private void loadClientsDataBase() {
        /*Almacena la información de la DB a memoria.*/
        String clientsFile = this.properties.getProperty("dbClientsFile");
        clients = new ArrayList<>();

        if (new File(clientsFile).exists()) {
            ReadFile(clientsFile);
        }
    }

    private void ReadFile(String file) {
        /*Encargado de leer el archivo ingresado por parámetro.
        Recorrerlo y almacenar sus datos en el atributo 'clients'*/

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new FileReader(file));
            br.readLine(); // elimino la cabecera
            while ((line = br.readLine()) != null) {
                String[] datos = line.split(cvsSplitBy);
                ClientValidation.ValidateClientDB(datos);

                ClientDTO client = new ClientDTO();
                long dni = Long.valueOf(datos[0]);
                client.setDni(dni);
                client.setName(datos[1]);
                client.setLastName(datos[2]);
                client.setEmail(datos[3]);
                int years = Integer.valueOf(datos[4]);
                client.setYears(years);
                client.setProvince(datos[5]);

                clients.add(client);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | InvalidInstanceDBException e) {
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

    private void saveClientDataBase() throws IOException {
        /*Encargado de 'actualizar' la DB. Almacena los datos guardados
        en memoria en el archivo correspondiente.*/

        String csvFile = this.properties.getProperty("dbClientsFile");
        FileWriter writer = new FileWriter(csvFile);

        String collect = this.properties.getProperty("initialClientCollect") + "\n";
        for (ClientDTO client : clients) {
            collect += client.getDni() + "," + client.getName() + "," + client.getLastName() + "," + client.getEmail() + ","
                    + client.getYears() + "," + client.getProvince() + "\n";
        }

        writer.write(collect);
        writer.close();
    }

    @Override
    public ClientDTO addClient(ClientDTO newClient) throws IOException, ExistsClientException {
        /*Encargado de incorporar un cliente a la DB, se mantiene actualizado tanto el archivo como la lista
        en memoria del mismo. En caso de existir el registro lanza una excepcion. */
        ClientDTO client = clients.stream().filter(c -> c.getDni() == newClient.getDni()).findAny().orElse(null);
        if (client == null) {
            clients.add(newClient);
            this.saveClientDataBase();
            return newClient;
        } else {
            String message = "El cliente con dni '" + newClient.getDni() + "' ya existe.";
            throw new ExistsClientException(message);
        }
    }

    @Override
    public List<ClientDTO> getClientsByFilter(Map<String, String> filters) {
        /*Encargada de devolver una lista de clientes que cumplan con el filtro enviado por parámetro. */
        List<ClientDTO> clientsFil = this.clients.stream().filter(
                client -> ((filters.get("name") == null || client.getName().toUpperCase().equals(filters.get("name").toUpperCase())) &&
                        (filters.get("lastName") == null || client.getLastName().toUpperCase().equals(filters.get("lastName").toUpperCase())) &&
                        (filters.get("email") == null || client.getEmail().toUpperCase().equals(filters.get("email").toUpperCase())) &&
                        (filters.get("years") == null || Integer.compare(client.getYears(), Integer.valueOf(filters.get("years"))) == 0) &&
                        (filters.get("dni") == null || Long.compare(client.getDni(), Long.valueOf(filters.get("dni"))) == 0) &&
                        (filters.get("province") == null || client.getProvince().toUpperCase().equals(filters.get("province").toUpperCase())))).collect(Collectors.toList());

        return clientsFil;
    }
}
