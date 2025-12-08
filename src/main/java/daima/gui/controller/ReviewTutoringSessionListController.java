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
import daima.business.dao.PeriodDAO;
import daima.business.dao.TutoringSessionDAO;
import daima.business.dto.TutoringSessionDTO;
import daima.business.dto.PeriodDTO;
import daima.business.dto.StaffDTO;
import daima.business.enumeration.TutoringSessionKind;
import daima.business.enumeration.TutoringSessionPlanState;
import daima.business.service.TutoringSessionService;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;

import java.util.ArrayList;

public class ReviewTutoringSessionListController extends Controller {
  @FXML
  private TableView<TutoringSessionDTO> tableTutoringSession;
  @FXML
  private TableColumn<TutoringSessionDTO, String> columnPeriod;
  @FXML
  private TableColumn<TutoringSessionDTO, String> columnFormattedTutored;
  @FXML
  private TableColumn<TutoringSessionDTO, TutoringSessionKind> columnKind;
  @FXML
  private TableColumn<TutoringSessionDTO, TutoringSessionPlanState> columnState;
  @FXML
  private TableColumn<TutoringSessionDTO, String> columnFormattedAppointmentDate;
  @FXML
  private TextField fieldSearch;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureSearch(ObservableList<TutoringSessionDTO> tutoringSessionDTOObservableList) {
    useConfigureSearch(fieldSearch, tutoringSessionDTOObservableList, tableTutoringSession);
  }

  public void configureTableColumns() {
    columnPeriod.setCellValueFactory(new PropertyValueFactory<>("periodDTO"));
    columnFormattedTutored.setCellValueFactory(new PropertyValueFactory<>("formattedTutored"));
    columnKind.setCellValueFactory(new PropertyValueFactory<>("kind"));
    columnState.setCellValueFactory(new PropertyValueFactory<>("state"));
    columnFormattedAppointmentDate.setCellValueFactory(new PropertyValueFactory<>("formattedAppointmentDate"));
  }

  public void setTableItems() {
    try {
      StaffDTO currentStaffDTO = AuthClient.getInstance().getCurrentStaff();
      PeriodDTO currentPeriodDTO = PeriodDAO.getInstance().getCurrentPeriod();
      ArrayList<TutoringSessionDTO> sessionDTOList = TutoringSessionDAO.getInstance().getAllByTutorAndPeriod(
        currentStaffDTO.getID(),
        currentPeriodDTO
      );

      if (sessionDTOList.isEmpty()) {
        AlertFacade.showErrorAndWait(
          "No es posible mostrar elementos porque no hay ninguno registrado en el sistema aún."
        );
      }

      ObservableList<TutoringSessionDTO> tutoringSessionDTOObservableList = FXCollections.observableList(sessionDTOList);
      tableTutoringSession.setItems(tutoringSessionDTOObservableList);
      configureSearch(tutoringSessionDTOObservableList);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "No ha sido posible recuperar información debido a un error en la base de datos, intente de nuevo más tarde"
      );
    }
  }

  public void onClickRegisterTutoringSession() {
    RegisterTutoringSessionController.displayRegisterTutoringSessionModal(this::setTableItems);
  }

  public void onClickManageTutoringSession() {
    getSelectedItemFromTable(tableTutoringSession).ifPresent(it ->
      RegisterTutoringSessionController.displayUpdateTutoringSessionModal(this::setTableItems, it)
    );
  }

  private void confirmAttendance(TutoringSessionDTO sessionDTO) {
    boolean shallConfirmAttendance = AlertFacade.showConfirmationAndWait(
      "¿Está seguro de que desea confirmar la asistencia de esta sesión? Ya no podrá cambiar su estado."
    );

    if (shallConfirmAttendance) {
      try {
        boolean failed = TutoringSessionService.getInstance().confirmAttendance(sessionDTO);

        if (failed) {
          AlertFacade.showErrorAndWait("No ha sido posible confirmar asistencia.");
        } else {
          AlertFacade.showSuccessAndWait("La asistencia ha sido confirmada exitosamente.");
          setTableItems();
        }
      } catch (UserDisplayableException e) {
        AlertFacade.showErrorAndWait(e);
      }
    }
  }

  public void onClickConfirmAttendance() {
    getSelectedItemFromTable(tableTutoringSession).ifPresent(this::confirmAttendance);
  }

  public static void navigateToTutoringSessionListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Sesiones de Tutoría", "GUIReviewTutoringSessionListPage");
  }
}