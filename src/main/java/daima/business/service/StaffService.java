package daima.business.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import daima.business.dao.CatalogDAO;
import daima.business.dao.StaffDAO;
import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;
import daima.common.BusinessRuleException;
import daima.common.ExceptionHandler;
import daima.common.UserDisplayableException;
import daima.db.DBConnector;

/**
 * StaffService is a singleton class that provides services related to staff management.
 * It includes methods for creating staff members and handling associated roles and tutors.
 */
public class StaffService {
  private static final Logger LOGGER = LogManager.getLogger(StaffService.class);
  private static final StaffService INSTANCE = new StaffService();

  public static StaffService getInstance() {
    return INSTANCE;
  }

  /**
   * Creates a new staff member along with their associated roles and tutors.
   *
   * @param staffDTO the StaffDTO object containing staff member details.
   * @throws UserDisplayableException if there is an error during the creation process.
   */
  public void createStaff(StaffDTO staffDTO) throws UserDisplayableException {
    try (Connection connection = DBConnector.getInstance().getConnection()) {
      connection.setAutoCommit(false);

      try {
        StaffDAO.getInstance().createOne(connection, staffDTO);
      } catch (SQLException e) {
        connection.rollback();
        throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear el miembro de personal.");
      }

      try {
        CatalogDAO.getInstance().createAllRolesForStaff(connection, staffDTO);
      } catch (SQLException e) {
        connection.rollback();
        throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear el miembro de personal.");
      }

      if (staffDTO.getRoles().contains(StaffRole.TUTOR)) {
        try {
          CatalogDAO.getInstance().createAllTutorsForStaff(connection, staffDTO);
        } catch (SQLException e) {
          connection.rollback();
          throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear el miembro de personal.");
        }
      }

      connection.commit();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear el miembro de personal.");
    }
  }

  public ArrayList<StaffDTO> getAllTutorByProgramForRegistration(int idProgram) throws UserDisplayableException {
    ArrayList<StaffDTO> tutorDTOList = StaffDAO.getInstance().getAllTutorByProgram(idProgram);

    if (tutorDTOList.isEmpty()) {
      throw new BusinessRuleException(
        "No es posible continuar porque no hay ningún tutor registrado en el sistema."
      );
    }

    return tutorDTOList;
  }

  public void updateStaff(StaffDTO staffDTO) throws UserDisplayableException {
    try (Connection connection = DBConnector.getInstance().getConnection()) {
      connection.setAutoCommit(false);

      try {
        StaffDAO.getInstance().updateOne(connection, staffDTO);
      } catch (SQLException e) {
        connection.rollback();
        throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible actualizar el miembro de personal.");
      }

      try {
        CatalogDAO.getInstance().replaceAllRolesForStaff(connection, staffDTO);
      } catch (SQLException e) {
        connection.rollback();
        throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible actualizar el miembro de personal.");
      }

      if (staffDTO.getRoles().contains(StaffRole.TUTOR)) {
        try {
          CatalogDAO.getInstance().replaceAllTutorsForStaff(connection, staffDTO);
        } catch (SQLException e) {
          connection.rollback();
          throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible actualizar el miembro de personal.");
        }
      }

      connection.commit();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible actualizar el miembro de personal.");
    }
  }
}
