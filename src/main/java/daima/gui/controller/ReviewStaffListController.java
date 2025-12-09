package daima.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import daima.business.dao.StaffDAO;
import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;

import java.util.ArrayList;

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
  private TableColumn<StaffDTO, StaffRole> columnFormattedRoles;
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
    columnFormattedRoles.setCellValueFactory(new PropertyValueFactory<>("roles"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void configureSearch(
    ObservableList<StaffDTO> staffDTOObservableList
  ) {
    useConfigureSearch(fieldSearch, staffDTOObservableList, tableStaff);
  }

  public void setTableItems() {
    try {
      ArrayList<StaffDTO> staffDTOList = StaffDAO.getInstance().getAll();

      if (staffDTOList.isEmpty()) {
        AlertFacade.showErrorAndWait(
          "No es posible mostrar elementos porque no hay ninguno registrado en el sistema aún."
        );
      }

      ObservableList<StaffDTO> staffDTOObservableList = FXCollections.observableList(staffDTOList);
      tableStaff.setItems(staffDTOObservableList);
      configureSearch(staffDTOObservableList);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "No ha sido posible recuperar información debido a un error en la base de datos, intente de nuevo más tarde"
      );
    }
  }

  public void onClickRegisterStaff() {
    RegisterStaffController.displayRegisterStaffModal(this::setTableItems);
  }

  public void onClickManageStaff() {
    getSelectedItemFromTable(tableStaff).ifPresent(it ->
      RegisterStaffController.displayManageStaffModal(this::setTableItems, it)
    );
  }

  public void deleteStaff(StaffDTO staffDTO) {
    boolean shallDelete = AlertFacade.showConfirmationAndWait(
      "¿Está seguro de que desea eliminar este miembro de personal permanentemente?"
    );

    if (shallDelete) {
      try {
        boolean failed = StaffDAO.getInstance().deleteOne(staffDTO);

        if (failed) {
          AlertFacade.showErrorAndWait("No ha sido posible eliminar el miembro de personal.");
        } else {
          AlertFacade.showSuccessAndWait("El miembro de personal ha sido eliminado exitosamente.");
          setTableItems();
        }
      } catch (UserDisplayableException e) {
        AlertFacade.showErrorAndWait(e);
      }
    }
  }

  public void onClickDeleteStaff() {
    getSelectedItemFromTable(tableStaff).ifPresent(this::deleteStaff);
  }

  public static void navigateToStaffListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Miembros de Personal", "GUIReviewStaffListPage");
  }
}