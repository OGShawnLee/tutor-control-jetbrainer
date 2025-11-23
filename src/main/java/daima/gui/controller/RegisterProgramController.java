package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import daima.business.dto.ProgramDTO;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class RegisterProgramController extends Controller implements ContextController<ProgramDTO> {
  @FXML
  private Label title;
  @FXML
  private TextField fieldName;
  @FXML
  private Label labelTagName;
  @FXML
  private TextField fieldAcronym;
  @FXML
  private Label labelTagAcronym;
  private ProgramDTO editProgramDTO;

  @Override
  public void setContext(ProgramDTO data) {
    editProgramDTO = data;
    loadEditData();
    configureTitle();
  }

  public void initialize() {
    cleanErrorLabels();
  }

  private void configureTitle() {
    if (editProgramDTO == null) return;

    title.setText("Modificar Programa Educativo");
  }

  private void loadEditData() {
    if (editProgramDTO == null) return;

    fieldName.setText(editProgramDTO.getName());
    fieldAcronym.setText(editProgramDTO.getAcronym());
  }

  private void cleanErrorLabels() {
    labelTagName.setText("");
    labelTagAcronym.setText("");
  }

  private boolean isInvalidData() {
    return true;
  }

  private ProgramDTO getProgramDTOFromInput() {
    return new ProgramDTO(
      fieldName.getText().trim(),
      fieldAcronym.getText().trim()
    );
  }


  private void updateProgram() {
    // TODO: Add Update Program
  }

  private void registerProgram() {
    // TODO: Add Register Program
  }

  public void onClickRegisterProgram() {
    cleanErrorLabels();
    AlertFacade.showSuccessAndWait("El programa educativo ha sido registrado exitosamente.");
  }

  public static void displayRegisterProgramModal(Runnable onClose) {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration(
        "Registrar Programa Educativo",
        "GUIRegisterProgramModal",
        onClose
      )
    );
  }
}
