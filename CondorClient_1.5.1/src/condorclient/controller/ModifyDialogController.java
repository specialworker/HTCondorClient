/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package condorclient.controller;

import condorclient.utilities.XMLHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author lianxiang
 */
public class ModifyDialogController extends GridPane {

    @FXML
    private Label messageLabel;

    @FXML
    private Label errInfo;
    @FXML
    private HBox actionParent;
    @FXML
    private Button cancelButton;
    @FXML
    private HBox okParent;
    @FXML
    private Button okButton;
    @FXML
    private TextField newNameField;
    private String newJobName = null;
    public String clusterId = null;
    public int exeModify = 0;

    /**
     * Initializes the controller class.
     */
    private Stage stage;

    @FXML
    private void modifyJobFired(ActionEvent event) {
        XMLHandler handler = new XMLHandler();
        handler.modifyJob(newJobName, clusterId);
        System.out.println(newJobName);
        exeModify = 1;
        stage.close();

    }

    @FXML
    private void cancelFired(ActionEvent event) {
        stage.close();
    }

    
    @FXML
    private void nameFieldFired(MouseEvent event) {
        newJobName = newNameField.getText();
        XMLHandler handler = new XMLHandler();

        Boolean isExist = handler.jobNameExist(newJobName);
        if (isExist) {
            errInfo.setText("任务名已经存在");
            okButton.setDisable(true);
        } else if (newJobName.equals("")) {
           errInfo.setText("");
            okButton.setDisable(true);

        } else {
            errInfo.setText("");
            okButton.setDisable(false);
        }

    }

    public void init(Stage modifyStage) {
        this.stage = modifyStage;
        okButton.setDisable(true);

    }

}
