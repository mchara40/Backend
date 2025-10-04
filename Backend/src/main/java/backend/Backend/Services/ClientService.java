package backend.Backend.Services;

import backend.Backend.DTOS.ClientDTO;
import backend.Backend.Entities.Client;

import java.util.List;

public interface ClientService {
    List<ClientDTO> getAllClients();
    Client getClientById(Long id);
    ClientDTO createClient(ClientDTO clientDTO);
    ClientDTO updateClient(Long id, ClientDTO clientDTO);
    void deleteClient(Long id);
}
