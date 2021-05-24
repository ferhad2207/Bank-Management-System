package com.farhad.controllers;

import com.farhad.App;
import com.farhad.database.DatabaseSource;
import com.farhad.models.Customer;
import com.farhad.utils.AlertUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.Optional;

import static com.farhad.utils.RegexValidationUtils.*;

public class UserSettingsController {
    @FXML
    private Separator topSeparator;
    @FXML
    private Separator bottomSeparator;

    @FXML
    private TextField customerNameTextField;
    @FXML
    private TextField customerPhoneNumberTextField;
    @FXML
    private TextField customerEmailTextField;
    @FXML
    private TextField customerLoginTextField;
    @FXML
    private TextField otherDetailsTextField;
    @FXML
    private Button changePasswordBtn;
    @FXML
    private Button updateCustomerInfoBtn;
    @FXML
    private Button deleteCustomerBtn;

    private Customer customer;

    public void initialize() {
        customer = DatabaseSource.getInstance().getCustomer();
        VBox.setMargin(topSeparator, new Insets(25, 0, 0, 0));
        VBox.setMargin(bottomSeparator, new Insets(0, 0, 25, 0));
        customerPhoneNumberTextField.promptTextProperty().bind(customer.phoneNumberProperty());
        customerNameTextField.promptTextProperty().bind(customer.nameProperty());
        customerEmailTextField.promptTextProperty().bind(customer.emailProperty());
        customerLoginTextField.promptTextProperty().bind(customer.loginProperty());
        otherDetailsTextField.promptTextProperty().bind(customer.otherDetailsProperty());
        changePasswordBtn.setCursor(Cursor.HAND);
        changePasswordBtn.setOnAction(this::changePassword);
        updateCustomerInfoBtn.setOnAction(this::updateCustomerInfo);
        updateCustomerInfoBtn.setCursor(Cursor.HAND);
        deleteCustomerBtn.setOnAction(this::deleteCustomer);
        deleteCustomerBtn.setCursor(Cursor.HAND);
    }

    private void changePassword(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.initModality(Modality.APPLICATION_MODAL);
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialogPane.setMinWidth(Region.USE_PREF_SIZE);
        dialogPane.setPrefHeight(200);
        dialogPane.setPrefWidth(500.0);
        dialogPane.getStylesheets().add(App.class.getResource("style/dialog_pane.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        FXMLLoader loader = new FXMLLoader(App.class.getResource("view/change_password_dialog.fxml"));
        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.APPLY);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.APPLY) {
            ChangePasswordDialogController controller = loader.getController();
            boolean successful = controller.changePassword();
            if (successful) {
                AlertUtils.showInfoAlert("Successful", "Password has been changed successfully", "");
            } else {
                AlertUtils.showErrorAlert("Incorrect Input", "Be sure you're fillling right informations",
                        "");
                changePassword(null);
            }
        }
    }

    private void updateCustomerInfo(ActionEvent event) {
        if (inputValidations()) {
            DatabaseSource.getInstance().setPrevCustomerLogin(customer.getLogin());
            // implement updating...
            customer.setName(customerNameTextField.getText().trim());
            customer.setPhoneNumber(modifyNumber(customerPhoneNumberTextField.getText()));
            customer.setEmail(customerEmailTextField.getText().trim());
            customer.setLogin(customerLoginTextField.getText().trim());
            customer.setOtherDetails(otherDetailsTextField.getText().trim());
            DatabaseSource.getInstance().updateCustomerInfo();

            AlertUtils.showInfoAlert("Successful", "Your information is updated successfully", "");
        }
    }

    private void deleteCustomer(ActionEvent event) {
        boolean confirm = AlertUtils.showConfirmationAlert("Customer deletion",
                "Are you sure to delete your customer account", "All information about you will be lost");
        if (confirm) {
            DatabaseSource.getInstance().deleteCustomer();
            try {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("view/login.fxml"));
                Parent root = loader.load();
                App.changeStageTitle("Login");
                App.setRoot(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean inputValidations() {
        if (!phoneNumberRegexValidation(customerPhoneNumberTextField)) {
            AlertUtils.showErrorAlert("Incorrect Input", "Phone number is not correct",
                    "Please provide your phone number like (+000)00-000-00-00");
            return false;
        }
        if (!emailRegexValidation(customerEmailTextField)) {
            AlertUtils.showErrorAlert("Incorrect Input", "Email is not correct",
                    "Please provide correct email address");
            return false;
        }
        if (!usernameRegexValidation(customerLoginTextField)) {
            AlertUtils.showErrorAlert("Incorrect Input", "Username is not correct",
                    "Requirements:\n" +
                            "Length should be greater than or equal to 8\n" +
                            "Username could contain only letters, digits, ., _\n" +
                            "Digits and . could not be the first character of username\n" +
                            ". could not appear consecutively");
            return false;
        }
        if (!phoneNumberDBValidation()) {
            AlertUtils.showErrorAlert("Incorrect Input", "Customer with this phone number is already exist", "");
            return false;
        }
        if (!emailDBValidation()) {
            AlertUtils.showErrorAlert("Incorrect Input", "Customer with this email is already registered", "");
            return false;
        }
        if (!loginDBValidation()) {
            AlertUtils.showErrorAlert("Incorrect Input", "Username is already token",
                    "Please take another username");
            return false;
        }
        return true;
    }

    private boolean phoneNumberDBValidation() {
        return !DatabaseSource.getInstance().customerDataExistsOnUpdate(customerPhoneNumberTextField.getText().trim(),
                DatabaseSource.COLUMN_CUSTOMER_PHONE);
    }

    private boolean emailDBValidation() {
        return !DatabaseSource.getInstance().customerDataExistsOnUpdate(customerEmailTextField.getText().trim(),
                DatabaseSource.COLUMN_CUSTOMER_EMAIL);
    }

    private boolean loginDBValidation() {
        return !DatabaseSource.getInstance().customerDataExistsOnUpdate(customerLoginTextField.getText().trim(),
                DatabaseSource.COLUMN_CUSTOMER_LOGIN);
    }
}
