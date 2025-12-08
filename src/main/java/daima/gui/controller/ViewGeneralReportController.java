package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import daima.business.dto.ReportDTO;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class ViewGeneralReportController extends Controller {
  @FXML
  private Label title;
  @FXML
  private TextField fieldPeriod;
  @FXML
  private TextField fieldTutoringSessionKind;
  @FXML
  private TextField fieldFormattedCreatedAt;
  @FXML
  private TextField fieldNameCoordinator;
  @FXML
  private TextField fieldState;
  @FXML
  private TextArea fieldContent;
  private ReportDTO reportDTO;

  private void setContext(ReportDTO reportDTO) {
    this.reportDTO = reportDTO;
    configureData();
  }

  private void configureData() {
    fieldPeriod.setText(reportDTO.getPeriodDTO().toString());
    fieldTutoringSessionKind.setText(reportDTO.getSessionKind().toString());
    fieldFormattedCreatedAt.setText(reportDTO.getFormattedCreatedAt());
    fieldNameCoordinator.setText(reportDTO.getNameStaff());
    fieldState.setText(reportDTO.getState().toString());
    fieldContent.setText(reportDTO.getContent());
  }

  public static void displayViewGeneralReportModal(ReportDTO reportDTO) {
    if (reportDTO == null) {
      throw new IllegalArgumentException("El reporte ha visualizar no puede ser nulo.");
    }

    try {
      ViewGeneralReportController controller = ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          "Ver Reporte General",
          "GUIViewGeneralReportModal"
        )
      );

      controller.setContext(reportDTO);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }

  @Override
  public void onClickClose() {
    super.close();
  }
}
