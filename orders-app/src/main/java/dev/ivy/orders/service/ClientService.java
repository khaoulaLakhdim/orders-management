package dev.ivy.orders.service;

import dev.ivy.orders.entity.Client;
import dev.ivy.orders.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(Long id) {
        return clientRepository.findById(id);
    }

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, Client clientDetails) {
        Optional<Client> optionalClient = clientRepository.findById(id);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            client.setName(clientDetails.getName());
            client.setCode(clientDetails.getCode());
            client.setCity(clientDetails.getCity());
            return clientRepository.save(client);
        }
        throw new RuntimeException("Client not found with id: " + id);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return clientRepository.existsById(id);
    }
} 