package daima.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import daima.business.dao.ProgramDAO;
import daima.business.dto.ProgramDTO;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class ReviewProgramListController extends Controller {
  @FXML
  private TableView<ProgramDTO> tableProgram;
  @FXML
  private TableColumn<ProgramDTO, String> columnProgramID;
  @FXML
  private TableColumn<ProgramDTO, String> columnName;
  @FXML
  private TableColumn<ProgramDTO, String> columnAcronym;
  @FXML
  private TableColumn<ProgramDTO, String> columnFormattedCreatedAt;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  public void configureTableColumns() {
    columnProgramID.setCellValueFactory(new PropertyValueFactory<>("ID"));
    columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    columnAcronym.setCellValueFactory(new PropertyValueFactory<>("acronym"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void setTableItems() {
    tableProgram.setItems(
      FXCollections.observableList(ProgramDAO.getInstance().getAll())
    );
  }

  public void onClickRegisterProgram() {
    ModalFacade.createAndDisplay(
      new ModalFacadeConfiguration(
        "Register Program",
        "GUIRegisterProgramModal",
        this::setTableItems
      )
    );
  }

  public void onClickManageProgram() {
    ProgramDTO selectedProgram = tableProgram.getSelectionModel().getSelectedItem();

    if (selectedProgram == null) {
      AlertFacade.showWarningAndWait(
        "Para realizar esta operación debe seleccionar una fila de la tabla."
      );
    } else {
      ModalFacade.createAndDisplayContextModal(
        new ModalFacadeConfiguration(
          "Modificar Programa Educativo",
          "GUIRegisterProgramModal",
          this::setTableItems
        ),
        selectedProgram
      );
    }
  }

  public static void navigateToProgramListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Programas", "GUIReviewProgramListPage");
  }
}