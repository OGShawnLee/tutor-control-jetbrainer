package daima.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import daima.business.dao.StaffDAO;
import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class ReviewStaffListController extends Controller {
  @FXML
  private TableView<StaffDTO> tableStaff;
  @FXML
  private TableColumn<StaffDTO, String> columnStaffID;
  @FXML
  private TableColumn<StaffDTO, String> columnName;
  @FXML
  private TableColumn<StaffDTO, String> columnLastName;
  @FXML
  private TableColumn<StaffDTO, String> columnEmail;
  @FXML
  private TableColumn<StaffDTO, StaffRole> columnRole;
  @FXML
  private TableColumn<StaffDTO, String> columnFormattedCreatedAt;
  @FXML
  private TextField fieldSearch;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnStaffID.setCellValueFactory(new PropertyValueFactory<>("ID"));
    columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    columnLastName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
    columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    columnRole.setCellValueFactory(new PropertyValueFactory<>("role"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void setTableItems() {
    tableStaff.setItems(
      FXCollections.observableList(StaffDAO.getInstance().getAll())
    );
  }

  public void onClickRegisterStaff() {
    RegisterStaffController.displayRegisterStaffModal(this::setTableItems);
  }

  public void onClickManageStaff() {
    StaffDTO selectedStaff = tableStaff.getSelectionModel().getSelectedItem();

    if (selectedStaff == null) {
      AlertFacade.showWarningAndWait(
        "Para realizar esta operación debe seleccionar una fila de la tabla."
      );
    } else {
      ModalFacade.createAndDisplayContextModal(
        new ModalFacadeConfiguration(
          "Modificar Miembro de Personal",
          "GUIRegisterStaffModal",
          this::setTableItems
        ),
        selectedStaff
      );
    }
  }

  public void onClickDeleteStaff() {
    StaffDTO selectedStaff = tableStaff.getSelectionModel().getSelectedItem();

    if (selectedStaff == null) {
      AlertFacade.showWarningAndWait(
        "Para realizar esta operación debe seleccionar una fila de la tabla."
      );
    } else {
      boolean shallDelete = AlertFacade.showConfirmationAndWait(
        "¿Está seguro de que desea eliminar este miembro de personal permanentemente?"
      );
      // TODO: Handle Delete
    }
  }

  public static void navigateToStaffListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Miembros de Personal", "GUIReviewStaffListPage");
  }
}