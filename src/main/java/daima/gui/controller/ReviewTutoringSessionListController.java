package daima.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import daima.business.dao.TutoringSessionDAO;
import daima.business.dto.TutoringSessionDTO;
import daima.business.enumeration.TutoringSessionKind;
import daima.business.enumeration.TutoringSessionPlanState;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class ReviewTutoringSessionListController extends Controller {
  @FXML
  private TableView<TutoringSessionDTO> tableTutoringSession;
  @FXML
  private TableColumn<TutoringSessionDTO, String> columnFormattedTutored;
  @FXML
  private TableColumn<TutoringSessionDTO, TutoringSessionKind> columnKind;
  @FXML
  private TableColumn<TutoringSessionDTO, TutoringSessionPlanState> columnState;
  @FXML
  private TableColumn<TutoringSessionDTO, String> columnFormattedAppointmentDate;
  @FXML
  private TableColumn<TutoringSessionDTO, String> columnPeriod;
  @FXML
  private TextField fieldSearch;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnFormattedTutored.setCellValueFactory(new PropertyValueFactory<>("formattedTutored"));
    columnKind.setCellValueFactory(new PropertyValueFactory<>("kind"));
    columnState.setCellValueFactory(new PropertyValueFactory<>("state"));
    columnFormattedAppointmentDate.setCellValueFactory(new PropertyValueFactory<>("formattedAppointmentDate"));
    columnPeriod.setCellValueFactory(new PropertyValueFactory<>("period"));
  }

  public void setTableItems() {
    tableTutoringSession.setItems(
      FXCollections.observableList(TutoringSessionDAO.getInstance().getAllByTutor(0))
    );
  }

  public void onClickRegisterTutoringSession() {
    RegisterTutoringSessionController.displayRegisterTutoringSessionModal(this::setTableItems);
  }

  public void onClickManageTutoringSession() {
    getSelectedItemFromTable(tableTutoringSession).ifPresent(tutoringSessionDTO ->
      ModalFacade.createAndDisplayContextModal(
        new ModalFacadeConfiguration(
          "Reagendar Sesión de Tutoría",
          "GUIRegisterTutoringSessionModal",
          this::setTableItems
        ),
        tutoringSessionDTO
      )
    );
  }

  public void onClickConfirmAttendance() {
    // TODO: Add Update Tutoring Session State
  }

  public void onClickConfirmAbsence() {
    // TODO: Add Update Tutoring Session State
  }

  public static void navigateToTutoringSessionListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Sesiones de Tutoría", "GUIReviewTutoringSessionListPage");
  }
}