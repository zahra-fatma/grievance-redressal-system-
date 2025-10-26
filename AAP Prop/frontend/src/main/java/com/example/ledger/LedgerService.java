package com.example.ledger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Immutable append-only ledger
 * Neo-Brutalist style: raw, minimal, tamper-proof
 */
public class LedgerService {

    // One transaction in the ledger
    public static class LedgerEntry {
        private final String user;
        private final String action;
        private final String hash;
        private final LocalDateTime timestamp;

        public LedgerEntry(String user, String action, String hash) {
            this.user = user;
            this.action = action;
            this.hash = hash;
            this.timestamp = LocalDateTime.now();
        }

        public String getUser() { return user; }
        public String getAction() { return action; }
        public String getHash() { return hash; }
        public LocalDateTime getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return "[" + timestamp + "] " + user + " -> " + action + " (hash=" + hash + ")";
        }
    }

    // The ledger list (append-only)
    private final List<LedgerEntry> ledger = new ArrayList<>();

    // 🔥 Append entry (this is what you were missing!)
    public void addEntry(String user, String action, String hash) {
        ledger.add(new LedgerEntry(user, action, hash));
    }

    // Get immutable copy
    public List<LedgerEntry> getEntries() {
        return Collections.unmodifiableList(ledger);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("=== Ledger ===\n");
        for (LedgerEntry e : ledger) {
            sb.append(e).append("\n");
        }
        return sb.toString();
    }
}