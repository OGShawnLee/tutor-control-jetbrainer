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
import daima.business.dao.TutoringSessionPlanDAO;
import daima.business.dto.StaffDTO;
import daima.business.dto.PeriodDTO;
import daima.business.dto.TutoringSessionPlanDTO;
import daima.business.enumeration.TutoringSessionKind;
import daima.business.enumeration.TutoringSessionPlanState;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;

import java.util.ArrayList;

public class ReviewTutoringSessionPlanListController extends Controller {
  @FXML
  private TableView<TutoringSessionPlanDTO> tableTutoringSessionPlan;
  @FXML
  private TableColumn<TutoringSessionPlanDTO, TutoringSessionKind> columnPeriod;
  @FXML
  private TableColumn<TutoringSessionPlanDTO, TutoringSessionKind> columnKind;
  @FXML
  private TableColumn<TutoringSessionPlanDTO, TutoringSessionPlanState> columnState;
  @FXML
  private TableColumn<TutoringSessionPlanDTO, String> columnAppointmentDate;
  @FXML
  private TableColumn<TutoringSessionPlanDTO, String> columnFormattedCreatedAt;
  @FXML
  private TextField fieldSearch;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnPeriod.setCellValueFactory(new PropertyValueFactory<>("periodDTO"));
    columnKind.setCellValueFactory(new PropertyValueFactory<>("kind"));
    columnState.setCellValueFactory(new PropertyValueFactory<>("state"));
    columnAppointmentDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void configureSearch(
    ObservableList<TutoringSessionPlanDTO> planDTOObservableList
  ) {
    useConfigureSearch(fieldSearch, planDTOObservableList, tableTutoringSessionPlan);
  }

  public void setTableItems() {
    try {
      StaffDTO currentStaffDTO = AuthClient.getInstance().getCurrentStaff();
      PeriodDTO currentPeriodDTO = PeriodDAO.getInstance().getCurrentPeriod();
      ArrayList<TutoringSessionPlanDTO> planDTOList = TutoringSessionPlanDAO.getInstance().getAllByProgramAndPeriod(
        currentStaffDTO.getProgramCoordinated().getID(),
        currentPeriodDTO
      );

      if (planDTOList.isEmpty()) {
        AlertFacade.showErrorAndWait(
          "No es posible mostrar elementos porque no hay ninguno registrado en el sistema aún."
        );
      }

      ObservableList<TutoringSessionPlanDTO> planDTOObservableList = FXCollections.observableList(planDTOList);
      tableTutoringSessionPlan.setItems(planDTOObservableList);
      configureSearch(planDTOObservableList);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "No ha sido posible recuperar información debido a un error en la base de datos, intente de nuevo más tarde."
      );
    }
  }

  public void onClickRegisterTutoringSessionPlan() {
    RegisterTutoringSessionPlanController.displayRegisterTutoringSessionPlanModal(this::setTableItems);
  }

  public static void navigateToTutoringSessionPlanListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Planeaciones de Tutoría", "GUIReviewTutoringSessionPlanListPage");
  }
}