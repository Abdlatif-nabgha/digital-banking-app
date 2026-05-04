package com.nabgha.digitalbanking.entities;

import com.nabgha.digitalbanking.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("current")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class CurrentAccount extends BankAccount {
    @Column(name = "over_draft")
    private double overDraft;
}
