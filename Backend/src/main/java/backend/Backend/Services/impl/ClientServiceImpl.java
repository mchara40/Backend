package backend.Backend.Services.impl;

import backend.Backend.DTOS.ClientDTO;
import backend.Backend.Entities.Client;
import backend.Backend.Repositories.ClientRepository;
import backend.Backend.Repositories.ProposalRepository;
import backend.Backend.Services.ClientService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {
    private ClientRepository clientRepository;
    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream().map(client -> {
            ClientDTO clientDTO = new ClientDTO();
            clientDTO.setId(client.getId());
            clientDTO.setClientName(client.getClientName());
            clientDTO.setPhone(client.getPhone());
            clientDTO.setAddress(client.getAddress());
            clientDTO.setEmail(client.getEmail());
            return clientDTO;
        }).toList();
    }

    @Override
    public Client getClientById(Long id) {
      return clientRepository.findById(id).orElse(null);
    }

    @Override
    public ClientDTO createClient(ClientDTO clientDTO) {
        Client client = new Client();
        client.setClientName(clientDTO.getClientName());
        client.setPhone(clientDTO.getPhone());
        client.setAddress(clientDTO.getAddress());
        client.setEmail(clientDTO.getEmail());
        Client savedClient = clientRepository.save(client);
        return new ClientDTO(savedClient.getId(), savedClient.getClientName(), savedClient.getPhone(), savedClient.getAddress(), savedClient.getEmail());
    }

    @Override
    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        Client client = clientRepository.findById(id).orElseThrow(()->new RuntimeException("Client is not exists with given id: " + id));
        client.setClientName(clientDTO.getClientName());
        client.setPhone(clientDTO.getPhone());
        client.setAddress(clientDTO.getAddress());
        client.setEmail(clientDTO.getEmail());
        Client savedClient = clientRepository.save(client);
        return new ClientDTO(savedClient.getId(), savedClient.getClientName(), savedClient.getPhone(), savedClient.getAddress(), savedClient.getEmail());
    }

    @Override
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id).orElseThrow(()->new RuntimeException("Client is not exists with given id: " + id));

        clientRepository.delete(client);
    }
}
