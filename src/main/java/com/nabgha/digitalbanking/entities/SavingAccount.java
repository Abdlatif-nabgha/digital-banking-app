package com.nabgha.digitalbanking.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@DiscriminatorValue("saving")
public class SavingAccount extends BankAccount {
    @Column(name = "interest_rate")
    private double interestRate;
}
