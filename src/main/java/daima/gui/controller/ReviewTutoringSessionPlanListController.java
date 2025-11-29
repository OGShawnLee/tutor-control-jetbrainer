package daima.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import daima.business.dao.TutoringSessionPlanDAO;
import daima.business.dto.TutoringSessionPlanDTO;
import daima.business.enumeration.TutoringSessionKind;
import daima.business.enumeration.TutoringSessionPlanState;

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
  private TableColumn<TutoringSessionPlanDTO, String> columnCoordinatorName;
  @FXML
  private TableColumn<TutoringSessionPlanDTO, String> columnFormattedCreatedAt;
  @FXML
  private TextField fieldSearch;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnPeriod.setCellValueFactory(new PropertyValueFactory<>("period"));
    columnKind.setCellValueFactory(new PropertyValueFactory<>("kind"));
    columnState.setCellValueFactory(new PropertyValueFactory<>("state"));
    columnAppointmentDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
    columnCoordinatorName.setCellValueFactory(new PropertyValueFactory<>("nameCoordinator"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void setTableItems() {
    tableTutoringSessionPlan.setItems(
      FXCollections.observableList(TutoringSessionPlanDAO.getInstance().getAllByProgram(0))
    );
  }

  public void onClickRegisterTutoringSessionPlan() {
    RegisterTutoringSessionPlanController.displayRegisterTutoringSessionPlanModal(this::setTableItems);
  }

  public static void navigateToTutoringSessionPlanListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Planeaciones de Tutoría", "GUIReviewTutoringSessionPlanListPage");
  }
}