package com.nabgha.digitalbanking.mappers;


import com.nabgha.digitalbanking.dtos.responses.OperationResponseDTO;
import com.nabgha.digitalbanking.entities.Operation;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper(componentModel = "spring")
public interface OperationMapper {

    // Entity -> Response DTO
    OperationResponseDTO toDto(Operation operation);

    List<OperationResponseDTO> toDtoList(List<Operation> operations);
}
