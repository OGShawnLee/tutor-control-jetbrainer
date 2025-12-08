package daima.gui.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import daima.business.AuthClient;
import daima.business.dao.TutoredDAO;
import daima.business.dto.TutoredDTO;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;

import java.util.ArrayList;

public class ReviewTutoredListTutorController extends ReviewTutoredListController {
  @FXML
  protected TableColumn<TutoredDTO, String> columnRiskState;

  @Override
  protected void configureTableColumns() {
    super.configureTableColumns();
    columnRiskState.setCellValueFactory(new PropertyValueFactory<>("riskState"));
  }

  public void setTableItems() {
    try {
      ArrayList<TutoredDTO> tutoredDTOList = TutoredDAO.getInstance().getAllByTutor(
        AuthClient.getInstance().getCurrentStaff().getID()
      );

      if (tutoredDTOList.isEmpty()) {
        AlertFacade.showErrorAndWait(
          "No es posible mostrar elementos porque no hay ninguno registrado en el sistema aún."
        );
      }

      ObservableList<TutoredDTO> tutoredDTOObservableList = FXCollections.observableList(tutoredDTOList);
      tableTutored.setItems(tutoredDTOObservableList);
      configureSearch(tutoredDTOObservableList);
    } catch (UserDisplayableException e) {
      AlertFacade.showErrorAndWait(
        "No ha sido posible recuperar información debido a un error en la base de datos, intente de nuevo más tarde"
      );
    }
  }

  public void onClickToggleState() {
    getSelectedItemFromTable(tableTutored).ifPresent(it -> {
      AlertFacade.showInformationAndWait("No es posible realizar esta acción ya que aún no ha sido implementada.");
    });
  }

  public static void navigateToTutoredListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Tutorados", "GUIReviewTutoredListTutorPage");
  }
}