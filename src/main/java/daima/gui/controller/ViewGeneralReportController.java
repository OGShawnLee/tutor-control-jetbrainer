package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import daima.business.dto.ReportDTO;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class ViewGeneralReportController extends Controller implements ContextController<ReportDTO> {
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
  private ReportDTO generalReportDTO;

  @Override
  public void setContext(ReportDTO data) {
    generalReportDTO = data;
    loadData();
  }

  private void loadData() {
    if (generalReportDTO == null) return;

    fieldPeriod.setText(generalReportDTO.getPeriod().toString());
    fieldTutoringSessionKind.setText(generalReportDTO.getTutoringSessionKind().toString());
    fieldFormattedCreatedAt.setText(generalReportDTO.getFormattedCreatedAt());
    fieldNameCoordinator.setText(generalReportDTO.getNameStaff());
    fieldState.setText(generalReportDTO.getState().toString());
    fieldContent.setText(generalReportDTO.getContent());
  }

  public static void displayViewGeneralReportModal(ReportDTO reportDTO) {
    ModalFacade.createAndDisplayContextModal(
      new ModalFacadeConfiguration(
        "Ver Reporte General",
        "GUIViewGeneralReportModal"
      ),
      reportDTO
    );
  }

  @Override
  public void onClickClose() {
    super.close();;
  }
}
