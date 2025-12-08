package daima.gui.modal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import daima.App;
import daima.common.ExceptionHandler;
import daima.common.UserDisplayableException;

public class ModalFacade {
  private final static Logger LOGGER = LogManager.getLogger(ModalFacade.class);

  public static FXMLLoader createFXMLLoader(String resourceFileName) throws IOException {
    return new FXMLLoader(
      Objects.requireNonNull(App.class.getResource("/" + resourceFileName + ".fxml"))
    );
  }

  public static <T> T displayModal(ModalFacadeConfiguration configuration) throws UserDisplayableException {
    try {
      FXMLLoader loader = createFXMLLoader(configuration.getResourceFileName());
      Parent parent = loader.load();
      Scene scene = new Scene(parent);
      Stage modalStage = new Stage();

      modalStage.setTitle(configuration.getTitle());
      modalStage.setScene(scene);
      modalStage.setResizable(false);
      modalStage.initModality(Modality.APPLICATION_MODAL);

      configuration.getOnClose().ifPresent(runnable ->
        modalStage.setOnHidden(event -> runnable.run())
      );

      modalStage.show();

      return loader.getController();
    } catch (IOException e) {
      throw new UserDisplayableException(
        ExceptionHandler.handleGUILoadIOException(LOGGER, e).getMessage()
      );
    }
  }
}