package daima.business.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import daima.business.dao.CatalogDAO;
import daima.business.dao.ProgramDAO;
import daima.business.dto.ProgramDTO;
import daima.common.BusinessRuleException;
import daima.common.ExceptionHandler;
import daima.common.UserDisplayableException;
import daima.db.DBConnector;

public class ProgramService {
  private static final Logger LOGGER = LogManager.getLogger(ProgramService.class);
  private static final ProgramService INSTANCE = new ProgramService();

  public static ProgramService getInstance() {
    return INSTANCE;
  }

  public void createProgram(ProgramDTO programDTO) throws UserDisplayableException {
    try (Connection connection = DBConnector.getInstance().getConnection()) {
      connection.setAutoCommit(false);

      try {
        ProgramDAO.getInstance().createOne(connection, programDTO);
      } catch (SQLException e) {
        connection.rollback();
        throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear el programa.");
      }

      if (programDTO.getIDCoordinator().isPresent()) {
        try {
          CatalogDAO.getInstance().replaceAllCoordinatesForProgram(connection, programDTO);
        } catch (SQLException e) {
          connection.rollback();
          throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear el programa.");
        }
      }

      connection.commit();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear el programa.");
    }
  }

  public ArrayList<ProgramDTO> getAllForRegistration() throws UserDisplayableException {
    ArrayList<ProgramDTO> programDTOList = ProgramDAO.getInstance().getAll();

    if (programDTOList.isEmpty()) {
      throw new BusinessRuleException(
        "No hay programas educativos registrados. Por favor, registre un programa educativo antes de continuar."
      );
    }

    return programDTOList;
  }

  public boolean updateProgram(ProgramDTO programDTO) throws UserDisplayableException {
    try (Connection connection = DBConnector.getInstance().getConnection()) {
      connection.setAutoCommit(false);

      boolean failed = false;

      try {
        failed = ProgramDAO.getInstance().updateOne(connection, programDTO);
      } catch (SQLException e) {
        connection.rollback();
        throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible modificar el programa.");
      }

      if (programDTO.getIDCoordinator().isPresent()) {
        try {
          CatalogDAO.getInstance().replaceAllCoordinatesForProgram(connection, programDTO);
        } catch (SQLException e) {
          connection.rollback();
          throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible modificar el programa.");
        }
      }

      connection.commit();
      return failed;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible actualizar el programa.");
    }
  }
}
