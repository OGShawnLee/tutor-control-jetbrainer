package daima.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import daima.business.AuthClient;
import daima.business.dao.ReportDAO;
import daima.business.dto.ReportDTO;
import daima.business.enumeration.ReportType;
import daima.business.enumeration.TutoringSessionKind;
import daima.business.enumeration.TutoringSessionPlanState;
import daima.business.service.GeneralReportService;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;

import java.util.ArrayList;

public class ReviewGeneralReportListController extends Controller {
  @FXML
  private TableView<ReportDTO> tableGeneralReport;
  @FXML
  private TableColumn<ReportDTO, String> columnPeriod;
  @FXML
  private TableColumn<ReportDTO, TutoringSessionKind> columnTutoringSessionKind;
  @FXML
  private TableColumn<ReportDTO, TutoringSessionPlanState> columnState;
  @FXML
  private TableColumn<ReportDTO, String> columnCoordinatorName;
  @FXML
  private TableColumn<ReportDTO, String> columnFormattedCreatedAt;
  @FXML
  private TextField fieldSearch;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnPeriod.setCellValueFactory(new PropertyValueFactory<>("periodDTO"));
    columnTutoringSessionKind.setCellValueFactory(new PropertyValueFactory<>("sessionKind"));
    columnState.setCellValueFactory(new PropertyValueFactory<>("state"));
    columnCoordinatorName.setCellValueFactory(new PropertyValueFactory<>("nameStaff"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void configureSearch(
    ObservableList<ReportDTO> reportDTOObservableList
  ) {
    useConfigureSearch(fieldSearch, reportDTOObservableList, tableGeneralReport);
  }

  public void setTableItems() {
    try {
      ArrayList<ReportDTO> reportDTOList = ReportDAO.getInstance().getAllByProgram(
        AuthClient.getInstance().getCurrentStaff().getProgramCoordinated().getID(),
        ReportType.GENERAL_REPORT
      );

      if (reportDTOList.isEmpty()) {
        AlertFacade.showErrorAndWait(
          "No es posible mostrar elementos porque no hay ninguno registrado en el sistema aún."
        );
      }

      ObservableList<ReportDTO> reportDTOObservableList = FXCollections.observableList(reportDTOList);

      tableGeneralReport.setItems(reportDTOObservableList);
      configureSearch(reportDTOObservableList);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "No ha sido posible recuperar información debido a un error en la base de datos, intente de nuevo más tarde."
      );
    }
  }

  public void onClickViewGeneralReport() {
    getSelectedItemFromTable(tableGeneralReport).ifPresent(
      ViewGeneralReportController::displayViewGeneralReportModal
    );
  }

  public void onClickRegisterReport() {
    RegisterGeneralReportController.displayRegisterReportModal(this::setTableItems);
  }

  public void onClickManageReport() {
    getSelectedItemFromTable(tableGeneralReport).ifPresent(it ->
      RegisterGeneralReportController.displayManageReportModal(this::setTableItems, it)
    );
  }

  private void deleteReport(ReportDTO reportDTO) {
    boolean shallDelete = AlertFacade.showConfirmationAndWait(
      "¿Está seguro de que desea eliminar este reporte permantemente? No podra recuperarlo."
    );

    if (shallDelete) {
      try {
        boolean failed = GeneralReportService.getInstance().deleteOne(reportDTO);

        if (failed) {
          AlertFacade.showErrorAndWait("No ha sido posible eliminar el reporte.");
        } else {
          AlertFacade.showSuccessAndWait("El reporte ha sido eliminado exitosamente.");
          setTableItems();
        }
      } catch (UserDisplayableException e) {
        AlertFacade.showErrorAndWait(e);
      }
    }
  }

  public void onClickDeleteReport() {
    getSelectedItemFromTable(tableGeneralReport).ifPresent(this::deleteReport);
  }

  private void sendReport(ReportDTO reportDTO) {
    boolean shallSend = AlertFacade.showConfirmationAndWait(
      "¿Está seguro de que desea enviar este reporte? Ya no podrá editarlo ni eliminarlo."
    );

    if (shallSend) {
      try {
        boolean failed = GeneralReportService.getInstance().sendOne(reportDTO);

        if (failed) {
          AlertFacade.showErrorAndWait("No ha sido posible enviar el reporte.");
        } else {
          AlertFacade.showSuccessAndWait("El reporte ha sido enviado exitosamente.");
          setTableItems();
        }
      } catch (UserDisplayableException e) {
        AlertFacade.showErrorAndWait(e);
      }
    }
  }

  public void onClickSendReport() {
    getSelectedItemFromTable(tableGeneralReport).ifPresent(this::sendReport);
  }

  public static void navigateToGeneralReportListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Reportes Generales", "GUIReviewGeneralReportListPage");
  }
}