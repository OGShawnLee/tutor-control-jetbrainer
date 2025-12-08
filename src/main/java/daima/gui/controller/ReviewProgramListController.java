package daima.gui.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import daima.business.dao.ProgramDAO;
import daima.business.dto.ProgramDTO;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;

import java.util.ArrayList;

public class ReviewProgramListController extends Controller {
  @FXML
  private TableView<ProgramDTO> tableProgram;
  @FXML
  private TableColumn<ProgramDTO, String> columnAcronym;
  @FXML
  private TableColumn<ProgramDTO, String> columnName;
  @FXML
  private TableColumn<ProgramDTO, String> columnNameCoordinator;
  @FXML
  private TableColumn<ProgramDTO, String> columnFormattedCreatedAt;
  @FXML
  private TextField fieldSearch;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureSearch(ObservableList<ProgramDTO> programDTOObservableList) {
    useConfigureSearch(fieldSearch, programDTOObservableList, tableProgram);
  }

  public void configureTableColumns() {
    columnAcronym.setCellValueFactory(new PropertyValueFactory<>("acronym"));
    columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    columnNameCoordinator.setCellValueFactory(data -> {
      String nameCoordinator = data.getValue().getNameCoordinator().orElse("Sin Coordinador Asignado");
      return new SimpleObjectProperty<>(nameCoordinator);
    });
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void setTableItems() {
    try {
      ArrayList<ProgramDTO> programDTOList = ProgramDAO.getInstance().getAll();

      if (programDTOList.isEmpty()) {
        AlertFacade.showErrorAndWait("No es posible mostrar elementos porque no hay ninguno registrado en el sistema aún.");
      }

      ObservableList<ProgramDTO> programDTOObservableList = FXCollections.observableArrayList(programDTOList);
      tableProgram.setItems(programDTOObservableList);
      configureSearch(programDTOObservableList);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "No ha sido posible recuperar información debido a un error en la base de datos, intente de nuevo más tarde."
      );
    }
  }

  public void onClickRegisterProgram() {
    RegisterProgramController.displayRegisterProgramModal(this::setTableItems);
  }

  public void onClickManageProgram() {
    getSelectedItemFromTable(tableProgram).ifPresent(it ->
      RegisterProgramController.displayManageProgramModal(this::setTableItems, it)
    );
  }

  public static void navigateToProgramListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Programas", "GUIReviewProgramListPage");
  }
}