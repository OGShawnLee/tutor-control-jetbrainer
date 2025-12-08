package daima.gui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import daima.App;
import daima.business.AuthClient;
import daima.common.ExceptionHandler;
import daima.gui.AlertFacade;

/*
 * Abstract base class for all controllers in the application.
 * Provides common functionality such as navigation between pages.
 * All controllers should extend this class to inherit its features.
 */
public abstract class Controller {
  private static final Logger CONTROLLER_LOGGER = LogManager.getLogger(Controller.class);
  // NOTE: Change this path if the project structure changes.
  private static final String VIEW_ROOT_PATH = "/";
  @FXML
  protected Node container;

  protected Stage getScene() {
    return (Stage) container.getScene().getWindow();
  }

  /**
   * Navigates the user to their respective landing page based on their role.
   */
  public void navigateToLandingPage() {
    switch (AuthClient.getInstance().getCurrentRole()) {
      case ADMIN:
        navigateFromThisPageTo("Landing Page", "GUILandingAdminPage");
        break;
      case COORDINATOR:
        navigateFromThisPageTo("Landing Page", "GUILandingCoordinatorPage");
        break;
      case SUPERVISOR:
        navigateFromThisPageTo("Landing Page", "GUILandingSupervisorPage");
        break;
      case TUTOR:
        navigateFromThisPageTo("Landing Page", "GUILandingTutorPage");
        break;
    }
  }

  /**
   * Navigates from the current page to the specified page.
   *
   * @param pageName         the name of the page to navigate to (for logging purposes).
   * @param resourceFileName the FXML file name of the target page (without extension).
   */
  protected void navigateFromThisPageTo(String pageName, String resourceFileName) {
    navigateTo(getScene(), pageName, resourceFileName);
  }

  /**
   * Navigates to the specified page, it replaces the current scene in the given stage.
   *
   * @param currentStage     the current stage to set the new scene on.
   * @param pageName         the name of the page to navigate to (for logging purposes).
   * @param resourceFileName the FXML file name of the target page (without extension).
   */
  protected static void navigateTo(Stage currentStage, String pageName, String resourceFileName) {
    try {
      Parent newView = FXMLLoader.load(
        Objects.requireNonNull(
          App.class.getResource(VIEW_ROOT_PATH + resourceFileName + ".fxml")
        )
      );
      Scene newScene = new Scene(newView);
      currentStage.setScene(newScene);
      currentStage.show();
    } catch (IOException e) {
      AlertFacade.showErrorAndWait(
        ExceptionHandler.handleGUILoadIOException(CONTROLLER_LOGGER, e).getMessage()
      );
    }
  }

  /**
   * Handles the action of closing the current window with a confirmation alert.
   */
  public void onClickClose() {
    boolean shallExit = AlertFacade.showConfirmationAndWait(
      "¿Está seguro de que desea abandonar la operación? Se perdera su progreso."
    );
    if (shallExit) close();
  }

  /**
   * Closes the current window.
   */
  protected void close() {
    Platform.runLater(() -> getScene().close());
  }

  public static <T> Optional<T> getSelectedItemFromTable(TableView<T> table) {
    return Optional.ofNullable(table.getSelectionModel().getSelectedItem());
  }
}
