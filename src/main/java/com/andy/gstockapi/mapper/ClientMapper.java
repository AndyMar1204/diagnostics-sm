package com.andy.gstockapi.mapper;

import com.andy.gstockapi.dto.ClientDTO;
import com.andy.gstockapi.entity.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientDTO toDto(Client client);
    Client toEntity(ClientDTO clientDTO);
}
