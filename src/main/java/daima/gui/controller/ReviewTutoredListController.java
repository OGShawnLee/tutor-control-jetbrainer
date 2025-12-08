package daima.gui.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import daima.business.dto.TutoredDTO;

public abstract class ReviewTutoredListController extends Controller {
  @FXML
  protected TableView<TutoredDTO> tableTutored;
  @FXML
  protected TableColumn<TutoredDTO, String> columnFormattedEnrollment;
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
  private ObservableList<TutoredDTO> tutoredDTOObservableList;

  public void initialize() {
    configureTableColumns();
    setTableItems();
  }

  protected void configureTableColumns() {
    columnFormattedEnrollment.setCellValueFactory(new PropertyValueFactory<>("formattedEnrollment"));
    columnFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
    columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    columnProgramName.setCellValueFactory(new PropertyValueFactory<>("programName"));
    columnFormattedCreatedAt.setCellValueFactory(new PropertyValueFactory<>("formattedCreatedAt"));
  }

  public void configureSearch(
    ObservableList<TutoredDTO> tutoredDTOObservableList
  ) {
    useConfigureSearch(fieldSearch, tutoredDTOObservableList, tableTutored);
  }

  protected abstract void setTableItems();
}