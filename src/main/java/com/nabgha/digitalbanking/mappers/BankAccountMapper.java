package com.nabgha.digitalbanking.mappers;

import com.nabgha.digitalbanking.dtos.responses.AccountResponseDTO;
import com.nabgha.digitalbanking.entities.BankAccount;
import com.nabgha.digitalbanking.entities.CurrentAccount;
import com.nabgha.digitalbanking.entities.SavingAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BankAccountMapper {

    // CurrentAccount -> AccountResponseDTO
    @Mapping(target = "type", constant = "CURRENT")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "interestRate", constant = "0.0")
    AccountResponseDTO currentAccountToDto(CurrentAccount currentAccount);

    // SavingAccount -> AccountResponseDto
    @Mapping(target = "type", constant = "SAVING")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "overDraft", constant = "0.0")
    AccountResponseDTO savingAccountToDto(SavingAccount savingAccount);

    // BankAccount generic -> dispatch to best mapper
    default AccountResponseDTO toDto(BankAccount bankAccount) {
        if (bankAccount instanceof CurrentAccount currentAccount) {
            return currentAccountToDto(currentAccount);
        }
        else if (bankAccount instanceof SavingAccount savingAccount) {
            return savingAccountToDto(savingAccount);
        }
        throw new  IllegalArgumentException("Unknown account type: " + bankAccount.getClass());

    }

    // List<BankAccount> → List<AccountResponseDTO>
    default List<AccountResponseDTO> toDtoList(List<BankAccount> accounts) {
        return accounts.stream()
                .map(this::toDto)
                .toList();
    }
}
