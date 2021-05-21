package com.farhad.controllers;

import com.farhad.App;
import com.farhad.utils.ActiveDashboardMenuItem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardMenuController {
    @FXML
    private HBox overviewHBox;
    @FXML
    private HBox accountsHBox;
    @FXML
    private HBox transactionsHBox;
    @FXML
    private HBox productsHBox;
    @FXML
    private HBox addAccountHBox;
    @FXML
    private HBox beMerchantHBox;
    @FXML
    private HBox userSettingsHBox;
    @FXML
    private HBox logoutHBox;

    @FXML
    private Label overviewLabel;
    @FXML
    private Label accountsLabel;
    @FXML
    private Label transactionsLabel;
    @FXML
    private Label productsLabel;
    @FXML
    private Label addAccountLabel;
    @FXML
    private Label beMerchantLabel;
    @FXML
    private Label userSettingsLabel;
    @FXML
    private Label logoutLabel;

    @FXML
    private Separator overviewSeparator;
    @FXML
    private Separator accountsSeparator;
    @FXML
    private Separator transactionsSeparator;
    @FXML
    private Separator productsSeparator;
    @FXML
    private Separator addAccountSeparator;
    @FXML
    private Separator beMerchantSeparator;
    @FXML
    private Separator userSettingsSeparator;
    @FXML
    private Separator logoutSeparator;


    public void initialize() {
        Object[][] menuItems = new Object[][]{
                {overviewHBox, overviewLabel, overviewSeparator},
                {accountsHBox, accountsLabel, accountsSeparator},
                {transactionsHBox, transactionsLabel, transactionsSeparator},
                {productsHBox, productsLabel, productsSeparator},
                {addAccountHBox, addAccountLabel, addAccountSeparator},
                {beMerchantHBox, beMerchantLabel, beMerchantSeparator},
                {userSettingsHBox, userSettingsLabel, userSettingsSeparator},
                {logoutHBox, logoutLabel, logoutSeparator}
        };
        for (Object[] menuItem : menuItems) {
            if (menuItem[0] == null) {
                System.out.println("Returned from init method of MenuController.java");
                return;
            }
            addUIInteractionToHBoxAndItsItems(
                    (HBox) menuItem[0], (Label) menuItem[1], (Separator) menuItem[2]
            );
        }
        overviewHBox.setOnMouseClicked(event -> {
            loadMenuItem("overview", ActiveDashboardMenuItem.OVERVIEW, "Overview");
        });
        accountsHBox.setOnMouseClicked(event -> {
            loadMenuItem("accounts", ActiveDashboardMenuItem.ACCOUNTS, "Accounts");
        });
    }

    public void addUIInteractionToHBoxAndItsItems(HBox hBox, Label label, Separator separator) {
        hBox.setOnMouseEntered(event -> {
            label.setTextFill(Color.WHITE);
            separator.setVisible(true);
            hBox.setCursor(Cursor.HAND);
        });
        hBox.setOnMouseExited(event -> {
            label.setTextFill(Color.valueOf("#444650"));
            separator.setVisible(false);
            hBox.setCursor(Cursor.HAND);
        });
    }

    private void loadMenuItem(String fxml, ActiveDashboardMenuItem menuItem, String stageName) {
        if (ActiveDashboardMenuItem.current != menuItem) {
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("view/" + fxml + ".fxml"));
                Parent root = loader.load();
                App.changeStageTitle(stageName);
                App.setRoot(root);
                ActiveDashboardMenuItem.current = menuItem;
            } catch (IOException e) {
                Logger.getLogger("IOException").log(Level.SEVERE, "Error has happened in " + fxml + ".fxml");
            }
        }
    }
}
