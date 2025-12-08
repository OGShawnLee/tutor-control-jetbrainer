package daima.gui.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

import daima.business.dao.TutoredDAO;
import daima.business.dto.TutoredDTO;
import daima.common.UserDisplayableException;
import daima.gui.AlertFacade;

import java.util.ArrayList;

public class ReviewTutoredListAdminController extends ReviewTutoredListController {
  @FXML
  protected TableColumn<TutoredDTO, String> columnTutorName;

  @Override
  protected void configureTableColumns() {
    super.configureTableColumns();
    columnTutorName.setCellValueFactory(data -> {
      String tutorName = data.getValue().getTutorName().orElse("Sin Tutor Asignado");
      return new SimpleObjectProperty<>(tutorName);
    });
  }

  public void setTableItems() {
    try {
      ArrayList<TutoredDTO> tutoredDTOList = TutoredDAO.getInstance().getAll();

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
        "No ha sido posible recuperar información debido a un error en la base de datos, intente de nuevo más tarde."
      );
    }
  }

  public void onClickRegisterTutored() {
    RegisterTutoredController.displayRegisterTutoredModal(this::setTableItems);
  }

  public void onClickManageTutored() {
    getSelectedItemFromTable(tableTutored).ifPresent(it ->
      RegisterTutoredController.displayManageTutoredModal(this::setTableItems, it)
    );
  }

  private void deleteTutored(TutoredDTO tutoredDTO) {
    boolean shallDelete = AlertFacade.showConfirmationAndWait(
      "¿Está seguro de que desea eliminar este tutorado permanentemente?"
    );

    if (shallDelete) {
      try {
        boolean failed = TutoredDAO.getInstance().deleteOne(tutoredDTO);

        if (failed) {
          AlertFacade.showErrorAndWait("No ha sido posible eliminar el tutorado.");
        } else {
          AlertFacade.showSuccessAndWait("El tutorado ha sido eliminado exitosamente.");
          setTableItems();
        }
      } catch (UserDisplayableException e) {
        AlertFacade.showErrorAndWait(
          "No ha sido posible eliminar el tutorado debido a un error en la base de datos, intente de nuevo más tarde."
        );
      }
    }

  }

  public void onClickDeleteTutored() {
    getSelectedItemFromTable(tableTutored).ifPresent(this::deleteTutored);
  }

  public static void navigateToTutoredListPage(Stage currentStage) {
    navigateTo(currentStage, "Administración de Tutorados", "GUIReviewTutoredListAdminPage");
  }
}