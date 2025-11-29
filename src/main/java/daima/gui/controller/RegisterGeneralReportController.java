package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import daima.business.dto.ReportDTO;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class RegisterGeneralReportController extends Controller implements ContextController<ReportDTO> {
  @FXML
  private Label title;
  @FXML
  private TextField fieldPeriod;
  @FXML
  private TextField fieldTutoringSessionKind;
  @FXML
  private TextArea fieldContent;
  @FXML
  private Label labelTagContent;
  private ReportDTO editReportDTO;

  @Override
  public void setContext(ReportDTO data) {
    editReportDTO = data;
    loadEditData();
    configureTitle();
  }

  public void initialize() {
    cleanErrorLabels();
  }

  private void configureTitle() {
    if (editReportDTO == null) return;

    title.setText("Modificar Reporte");
  }

  private void loadEditData() {
    if (editReportDTO == null) return;

    fieldContent.setText(editReportDTO.getContent());
    fieldTutoringSessionKind.setText(editReportDTO.getTutoringSessionKind().toString());
  }

  private void cleanErrorLabels() {
    labelTagContent.setText("");
  }

  private boolean isInvalidData() {
    return true;
  }

  private ReportDTO getReportDTOFromInput() {
    return new ReportDTO();
  }

  private void registerReport() {
    // TODO: Add Register Report
  }

  public void onClickRegisterGeneralReport() {
    cleanErrorLabels();
    AlertFacade.showSuccessAndWait("El reporte ha sido registrado exitosamente.");
  }

  public static void displayRegisterReportModal(Runnable onClose) {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration(
        "Registrar Reporte General",
        "GUIRegisterGeneralReportModal",
        onClose
      )
    );
  }
}
