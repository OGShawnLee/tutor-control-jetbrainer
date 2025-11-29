package daima.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import daima.business.dao.ReportDAO;
import daima.business.dto.ReportDTO;
import daima.business.enumeration.ReportType;
import daima.business.enumeration.TutoringSessionKind;
import daima.business.enumeration.TutoringSessionPlanState;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

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
    columnPeriod.setCellValueFactory(new PropertyValueFactory<>("period"));
    columnTutoringSessionKind.setCellValueFactory(new PropertyValueFactory<>("tutoringSessionKind"));
    columnState.setCellValueFactory(new PropertyValueFactory<>("state"));
    columnCoordinatorName.setCellValueFactory(new PropertyValueFactory<>("nameStaff"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void setTableItems() {
    tableGeneralReport.setItems(
      FXCollections.observableList(ReportDAO.getInstance().getAllByProgram(0, ReportType.GENERAL_REPORT))
    );
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
    getSelectedItemFromTable(tableGeneralReport).ifPresent(generalReportDTO ->
      ModalFacade.createAndDisplayContextModal(
        new ModalFacadeConfiguration(
          "Modificar Reporte General",
          "GUIRegisterGeneralReportModal",
          this::setTableItems
        ),
        generalReportDTO
      )
    );
  }

  public void onClickSendReport() {
    // TODO: Add Send General Report
  }

  public static void navigateToGeneralReportListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Reportes Generales", "GUIReviewGeneralReportListPage");
  }
}