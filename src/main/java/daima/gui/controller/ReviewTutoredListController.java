package daima.gui.controller;

import daima.business.enumeration.TutoredState;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import daima.business.dao.TutoredDAO;
import daima.business.dto.TutoredDTO;
import daima.gui.AlertFacade;
import daima.gui.modal.ModalFacade;
import daima.gui.modal.ModalFacadeConfiguration;

public abstract class ReviewTutoredListController extends Controller {
  @FXML
  protected TableView<TutoredDTO> tableTutored;
  @FXML
  protected TableColumn<TutoredDTO, String> columnEnrollment;
  @FXML
  protected TableColumn<TutoredDTO, String> columnFullName;
  @FXML
  protected TableColumn<TutoredDTO, String> columnEmail;
  @FXML
  protected TableColumn<TutoredDTO, String> columnProgramName;
  @FXML
  protected TableColumn<TutoredDTO, String> columnFormattedCreatedAt;
  @FXML
  protected TextField fieldSearch;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  protected void configureTableColumns() {
    columnEnrollment.setCellValueFactory(new PropertyValueFactory<>("enrollment"));
    columnFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
    columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    columnProgramName.setCellValueFactory(new PropertyValueFactory<>("programName"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  protected abstract void setTableItems();
}