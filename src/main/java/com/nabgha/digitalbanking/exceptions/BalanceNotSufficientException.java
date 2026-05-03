package com.nabgha.digitalbanking.exceptions;

/**
 * @auther abdlatif-nabgha
 **/
public class BalanceNotSufficientException extends Exception {
    public BalanceNotSufficientException(String message) {
        super(message);
    }
}
