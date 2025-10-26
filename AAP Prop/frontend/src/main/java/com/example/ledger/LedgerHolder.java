package com.example.ledger;

/**
 * LedgerHolder
 * Provides a single shared instance of LedgerService across the entire app.
 * Neo-Brutalist: raw, simple, functional.
 */
public class LedgerHolder {

    // Singleton instance of LedgerService
    private static final LedgerService INSTANCE = new LedgerService();

    // Private constructor → prevents instantiation
    private LedgerHolder() {}

    // Access point
    public static LedgerService getInstance() {
        return INSTANCE;
    }
}