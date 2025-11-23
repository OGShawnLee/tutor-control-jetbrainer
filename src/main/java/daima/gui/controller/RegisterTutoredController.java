package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import daima.business.dao.ProgramDAO;
import daima.business.dto.ProgramDTO;
import daima.business.dto.TutoredDTO;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

import java.util.ArrayList;

public class RegisterTutoredController extends Controller implements ContextController<TutoredDTO> {
  @FXML
  private Label title;
  @FXML
  private TextField fieldName;
  @FXML
  private Label labelTagName;
  @FXML
  private TextField fieldLastName;
  @FXML
  private Label labelTagLastName;
  @FXML
  private TextField fieldEmail;
  @FXML
  private Label labelTagEmail;
  @FXML
  private TextField fieldEnrollment;
  @FXML
  private Label labelTagEnrollment;
  @FXML
  private ComboBox<ProgramDTO> fieldProgram;
  @FXML
  private Label labelTagProgram;
  private TutoredDTO editTutoredDTO;

  @Override
  public void setContext(TutoredDTO data) {
    editTutoredDTO = data;
    loadEditData();
    configureTitle();
  }

  public void initialize() {
    cleanErrorLabels();
    configureFieldProgram();
  }

  private void configureTitle() {
    if (editTutoredDTO == null) return;

    title.setText("Modificar Tutorado");
  }

  private void loadEditData() {
    if (editTutoredDTO == null) return;

    fieldName.setText(editTutoredDTO.getName());
    fieldLastName.setText(editTutoredDTO.getLastName());
    fieldEmail.setText(editTutoredDTO.getEmail());
    fieldEnrollment.setText(editTutoredDTO.getEnrollment());
  }

  private void configureFieldProgram() {
    ArrayList<ProgramDTO> programDTOList = ProgramDAO.getInstance().getAll();

    fieldProgram.getItems().setAll(programDTOList);
    fieldProgram.setValue(programDTOList.get(0));
  }

  private void cleanErrorLabels() {
    labelTagName.setText("");
    labelTagLastName.setText("");
    labelTagEmail.setText("");
    labelTagEnrollment.setText("");
    labelTagProgram.setText("");
  }

  private boolean isInvalidData() {
    return true;
  }

  private TutoredDTO getTutoredDTOFromInput() {
    return new TutoredDTO(
      fieldName.getText(),
      fieldLastName.getText(),
      fieldEmail.getText(),
      fieldEnrollment.getText(),
      fieldProgram.getValue().getID()
    );
  }

  private void registerTutored() {
    // TODO: Add Register Tutored
  }

  public void onClickRegisterStaff() {
    cleanErrorLabels();
    AlertFacade.showSuccessAndWait("El tutorado ha sido registrado exitosamente.");
  }

  public static void displayRegisterTutoredModal(Runnable onClose) {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration(
        "Registrar Tutorado",
        "GUIRegisterTutoredModal",
        onClose
      )
    );
  }
}
