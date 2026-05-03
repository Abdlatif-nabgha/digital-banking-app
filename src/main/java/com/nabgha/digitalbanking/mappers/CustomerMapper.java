package com.nabgha.digitalbanking.mappers;

import com.nabgha.digitalbanking.dtos.requests.CustomerRequestDTO;
import com.nabgha.digitalbanking.dtos.responses.CustomerResponseDTO;
import com.nabgha.digitalbanking.entities.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    // RequestDto -> Entity
    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerRequestDTO customerRequestDTO);

    // Entity -> ResponseDto
    CustomerResponseDTO toDto(Customer customer);

    List<CustomerResponseDTO> toDtoList(List<Customer> customers);
}
