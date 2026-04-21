package com.andy.gstockapi.service;

import com.andy.gstockapi.dto.ClientDTO;
import com.andy.gstockapi.entity.Client;
import com.andy.gstockapi.exception.ResourceNotFoundException;
import com.andy.gstockapi.mapper.ClientMapper;
import com.andy.gstockapi.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    public ClientDTO getClientById(Integer id) {
        return clientRepository.findById(id)
                .map(clientMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + id));
    }

    @Transactional
    public ClientDTO createClient(ClientDTO clientDTO) {
        Client client = clientMapper.toEntity(clientDTO);
        return clientMapper.toDto(clientRepository.save(client));
    }

    @Transactional
    public ClientDTO updateClient(Integer id, ClientDTO clientDTO) {
        Client existingClient = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + id));

        existingClient.setName(clientDTO.getName());
        existingClient.setPhone(clientDTO.getPhone());
        existingClient.setEmail(clientDTO.getEmail());
        existingClient.setAddress(clientDTO.getAddress());

        return clientMapper.toDto(clientRepository.save(existingClient));
    }

    @Transactional
    public void deleteClient(Integer id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client non trouvé avec l'ID: " + id);
        }
        clientRepository.deleteById(id);
    }

    public List<ClientDTO> searchClients(String query) {
        // Simple search by name or phone
        return clientRepository.findAll().stream()
                .filter(c -> (c.getName() != null && c.getName().toLowerCase().contains(query.toLowerCase())) ||
                             (c.getPhone() != null && c.getPhone().contains(query)))
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }
}
