package com.example;

import com.example.ledger.LedgerHolder;
import com.example.ledger.LedgerService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class AdminController {

    @FXML
    private TextArea ledgerViewArea;

    @FXML
    protected void handleViewLedger() {
        LedgerService ledger = LedgerHolder.getInstance();
        ledgerViewArea.setText(ledger.toString());
    }
}