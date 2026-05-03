package com.nabgha.digitalbanking.exceptions;

/**
 * @auther abdlatif-nabgha
 **/
public class BankAccountNotFoundException extends Exception {
    public BankAccountNotFoundException(String message) {
        super(message);
    }
}
