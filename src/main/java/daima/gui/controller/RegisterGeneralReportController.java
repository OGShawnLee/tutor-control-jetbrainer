package daima.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import daima.business.AuthClient;
import daima.business.dao.ReportDAO;
import daima.business.dto.ProgramDTO;
import daima.business.dto.ReportDTO;
import daima.business.dto.TutoringSessionPlanDTO;
import daima.business.enumeration.ReportType;
import daima.business.service.GeneralReportService;
import daima.business.validator.ValidationResult;
import daima.business.validator.Validator;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

import java.util.AbstractMap.SimpleEntry;

public class RegisterGeneralReportController extends Controller {
  @FXML
  private Label title;
  @FXML
  private TextField fieldTutoringSessionKind;
  @FXML
  private TextArea fieldContent;
  @FXML
  private Label labelTagContent;
  private ReportDTO editReportDTO;
  private TutoringSessionPlanDTO currentPlanDTO;

  private void setContext(TutoringSessionPlanDTO currentPlanDTO, ReportDTO editReportDTO) {
    if (currentPlanDTO == null && editReportDTO == null) {
      throw new IllegalArgumentException(
        "La última planeación de tutoría y el reporte general a editar no pueden ser ambos nulos."
      );
    }

    this.currentPlanDTO = currentPlanDTO;
    this.editReportDTO = editReportDTO;
    configureTitle();
    configureCurrentPlanData();
    loadEditData();
  }

  public void initialize() {
    cleanErrorLabels();
  }

  private void configureCurrentPlanData() {
    if (currentPlanDTO == null) {
      fieldTutoringSessionKind.setText(editReportDTO.getSessionKind().toString());
    } else {
      fieldTutoringSessionKind.setText(currentPlanDTO.getKind().toString());
    }
  }

  private void configureTitle() {
    if (editReportDTO == null) {
      title.setText("Registrar Reporte General");
    } else {
      title.setText("Modificar Reporte General");
    }
  }

  private void loadEditData() {
    if (editReportDTO == null) return;

    fieldContent.setText(editReportDTO.getContent());
    fieldTutoringSessionKind.setText(editReportDTO.getSessionKind().toString());
  }

  private void cleanErrorLabels() {
    labelTagContent.setText("");
  }

  private boolean isInvalidData() {
    boolean isInvalidData = false;

    ValidationResult<String> result = Validator.getContentValidationResult(fieldContent.getText());
    if (result.isInvalid()) {
      labelTagContent.setText(result.getError());
      isInvalidData = true;
    }

    return isInvalidData;
  }

  private ReportDTO getReportDTOFromInput() {
    ReportDTO reportDTO = editReportDTO == null ? new ReportDTO() : editReportDTO;

    if (editReportDTO == null) {
      reportDTO.setIDStaff(AuthClient.getInstance().getCurrentStaff().getID());
      reportDTO.setIDSessionPlan(currentPlanDTO.getID());
      reportDTO.setContent(fieldContent.getText().trim());
      reportDTO.setType(ReportType.GENERAL_REPORT);
    } else {
      reportDTO.setContent(fieldContent.getText().trim());
    }

    return reportDTO;
  }

  private boolean getUpdateConfirmation() {
    String oldContent = editReportDTO.getContent();
    String newContent = fieldContent.getText().trim();

    if (oldContent.equals(newContent)) {
      return false;
    }

    return AlertFacade.showConfirmationAndWait(
      "¿Está seguro de que desea cambiar el contenido de su reporte general?"
    );
  }

  private void registerReport() throws UserDisplayableException {
    ReportDAO.getInstance().createOne(getReportDTOFromInput());
    AlertFacade.showSuccessAndWait("El reporte ha sido registrado exitosamente.");
    close();
  }

  private void updateReport() throws UserDisplayableException {
    if (getUpdateConfirmation()) {
      boolean failed = ReportDAO.getInstance().updateOne(getReportDTOFromInput());

      if (failed) {
        AlertFacade.showErrorAndWait("No ha sido posible modificar el reporte general.");
      } else {
        AlertFacade.showSuccessAndWait("El reporte general ha sido modificado exitosamente.");
        close();
      }
    }
  }

  public void onClickRegisterGeneralReport() {
    cleanErrorLabels();

    if (isInvalidData()) return;

    try {
      if (editReportDTO == null) {
        registerReport();
      } else {
        updateReport();
      }
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e.getMessage());
    }
  }

  public static void displayRegisterReportModal(Runnable onClose) {
    try {
      ProgramDTO coordinatedProgramDTO = AuthClient.getInstance().getCurrentStaff().getProgramCoordinated();
      TutoringSessionPlanDTO latestPlanDTO = GeneralReportService
        .getInstance()
        .getLatestSessionPlanForRegistration(
          coordinatedProgramDTO.getID()
        );

      RegisterGeneralReportController controller = ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          "Registrar Reporte General",
          "GUIRegisterGeneralReportModal",
          onClose
        )
      );

      controller.setContext(latestPlanDTO, null);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }

  public static void displayManageReportModal(Runnable onClose, ReportDTO reportDTO) {
    try {
      SimpleEntry<TutoringSessionPlanDTO, ReportDTO> pair = GeneralReportService
        .getInstance()
        .getOneForUpdate(reportDTO.getID());

      RegisterGeneralReportController controller = ModalFacade.displayModal(
        new ModalFacadeConfiguration(
          "Registrar Reporte General",
          "GUIRegisterGeneralReportModal",
          onClose
        )
      );

      controller.setContext(pair.getKey(), pair.getValue());
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(e);
    }
  }
}
