package daima.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import daima.business.dao.TutoredDAO;
import daima.business.dto.TutoredDTO;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class ReviewTutoredListController extends Controller {
  @FXML
  private TableView<TutoredDTO> tableTutored;
  @FXML
  private TableColumn<TutoredDTO, String> columnTutoredID;
  @FXML
  private TableColumn<TutoredDTO, String> columnName;
  @FXML
  private TableColumn<TutoredDTO, String> columnLastName;
  @FXML
  private TableColumn<TutoredDTO, String> columnEmail;
  @FXML
  private TableColumn<TutoredDTO, String> columnProgramName;
  @FXML
  private TableColumn<TutoredDTO, String> columnFormattedCreatedAt;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnTutoredID.setCellValueFactory(new PropertyValueFactory<>("ID"));
    columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    columnLastName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
    columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    columnProgramName.setCellValueFactory(new PropertyValueFactory<>("programName"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void setTableItems() {
    tableTutored.setItems(
      FXCollections.observableList(TutoredDAO.getInstance().getAll())
    );
  }

  public void onClickRegisterTutored() {
    RegisterTutoredController.displayRegisterTutoredModal(this::setTableItems);
  }

  public void onClickManageTutored() {
    TutoredDTO selectedTutored = tableTutored.getSelectionModel().getSelectedItem();

    if (selectedTutored == null) {
      AlertFacade.showWarningAndWait(
        "Para realizar esta operación debe seleccionar una fila de la tabla."
      );
    } else {
      ModalFacade.createAndDisplayContextModal(
        new ModalFacadeConfiguration(
          "Modificar Empleado",
          "GUIRegisterTutoredModal",
          this::setTableItems
        ),
        selectedTutored
      );
    }
  }

  public void onClickDeleteTutored() {
    TutoredDTO selectedTutored = tableTutored.getSelectionModel().getSelectedItem();

    if (selectedTutored == null) {
      AlertFacade.showWarningAndWait(
        "Para realizar esta operación debe seleccionar una fila de la tabla."
      );
    } else {
      boolean shallDelete = AlertFacade.showConfirmationAndWait(
        "¿Está seguro de que desea eliminar este tutorado permanentemente?"
      );
      // TODO: Handle Delete
    }
  }

  public static void navigateToTutoredListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Tutorados", "GUIReviewTutoredListPage");
  }
}