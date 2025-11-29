package daima.gui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import daima.business.dao.TutoredDAO;
import daima.business.dto.TutoredDTO;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public class ReviewTutoredListCoordinatorController extends ReviewTutoredListController {
  @FXML
  protected TableColumn<TutoredDTO, String> columnTutorName;

  @Override
  protected void configureTableColumns() {
    super.configureTableColumns();
    columnTutorName.setCellValueFactory(new PropertyValueFactory<>("tutorName"));
  }

  public void setTableItems() {
    tableTutored.setItems(
      FXCollections.observableList(TutoredDAO.getInstance().getAll())
    );
  }

  public void onClickAssignTutored() {
    TutoredDTO selectedTutored = tableTutored.getSelectionModel().getSelectedItem();

    if (selectedTutored == null) {
      AlertFacade.showWarningAndWait(
        "Para realizar esta operación debe seleccionar una fila de la tabla."
      );
    } else {
      AssignTutoredController.displayAssignTutoredModal(selectedTutored, this::setTableItems);
    }
  }

  public void onClickReassignTutored() {
    TutoredDTO selectedTutored = tableTutored.getSelectionModel().getSelectedItem();

    if (selectedTutored == null) {
      AlertFacade.showWarningAndWait(
        "Para realizar esta operación debe seleccionar una fila de la tabla."
      );
    } else {
      ModalFacade.createAndDisplayContextModal(
        new ModalFacadeConfiguration(
          "Reasignar Tutorado",
          "GUIAssignTutoredModal",
          this::setTableItems
        ),
        selectedTutored
      );
    }
  }

  public static void navigateToTutoredListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Tutorados", "GUIReviewTutoredListCoordinatorPage");
  }
}