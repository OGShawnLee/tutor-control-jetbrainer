package daima.business.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import daima.business.dto.TutoredDTO;
import daima.business.enumeration.TutoredState;
import daima.common.ExceptionHandler;
import daima.common.InvalidFieldException;
import daima.common.UserDisplayableException;
import daima.db.DBConnector;

public class TutoredDAO extends DAOShape<TutoredDTO> {
  private static final Logger LOGGER = LogManager.getLogger(TutoredDAO.class);
  private static final String CREATE_ONE_QUERY =
    "INSERT INTO Tutored (name, last_name, email, enrollment, program_id) VALUES (?, ?, ?, ?, ?)";
  private static final String FIND_ONE_BY_ENROLLMENT_QUERY = "SELECT * FROM CompleteTutoredView WHERE enrollment = ?";
  private static final String GET_ALL_QUERY = "SELECT * FROM CompleteTutoredView ORDER BY created_at";
  private static final String GET_ALL_BY_TUTOR_QUERY = "SELECT * FROM CompleteTutoredView WHERE tutor_id = ? ORDER BY created_at";
  private static final String GET_ALL_BY_PROGRAM_QUERY = "SELECT * FROM CompleteTutoredView WHERE program_id = ? ORDER BY created_at";
  private static final String GET_ALL_BY_PROGRAM_AND_TUTOR_QUERY = "SELECT * FROM CompleteTutoredView WHERE program_id = ? AND tutor_id = ? ORDER BY created_at";
  private static final String GET_ONE_QUERY = "SELECT * FROM CompleteTutoredView WHERE tutored_id = ?";
  private static final String UPDATE_ONE_QUERY =
    "UPDATE Tutored SET name = ?, last_name = ?, email = ?, enrollment = ?, state = ?, program_id = ?, tutor_id = ? WHERE tutored_id = ?";
  private static final String DELETE_ONE_QUERY = "DELETE FROM Tutored WHERE tutored_id = ?";
  private static final TutoredDAO INSTANCE = new TutoredDAO();

  public static TutoredDAO getInstance() {
    return INSTANCE;
  }

  @Override
  public TutoredDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws SQLException, InvalidFieldException {
    TutoredDTO tutoredDTO = new TutoredDTO();
    tutoredDTO.setID(resultSet.getInt("tutored_id"));
    tutoredDTO.setName(resultSet.getString("name"));
    tutoredDTO.setLastName(resultSet.getString("last_name"));
    tutoredDTO.setEnrollment(resultSet.getString("enrollment"));
    tutoredDTO.setRiskState(TutoredState.valueOf(resultSet.getString("state")));
    tutoredDTO.setIDProgram(resultSet.getInt("program_id"));
    tutoredDTO.setProgramName(resultSet.getString("program_name"));
    tutoredDTO.setIDTutor(resultSet.getInt("tutor_id"));
    tutoredDTO.setTutorName(resultSet.getString("tutor_name"));
    tutoredDTO.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
    return tutoredDTO;
  }

  public void createOne(TutoredDTO tutoredDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(CREATE_ONE_QUERY)
    ) {
      statement.setString(1, tutoredDTO.getName());
      statement.setString(2, tutoredDTO.getLastName());
      statement.setString(3, tutoredDTO.getEmail());
      statement.setString(4, tutoredDTO.getEnrollment());
      statement.setInt(5, tutoredDTO.getIDProgram());

      statement.executeUpdate();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear el tutorado.");
    }
  }

  public Optional<TutoredDTO> findOneByEnrollment(String enrollment) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(FIND_ONE_BY_ENROLLMENT_QUERY)
    ) {
      statement.setString(1, enrollment);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return Optional.of(createDTOInstanceFromResultSet(resultSet));
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar el tutorado.");
    }
  }

  public ArrayList<TutoredDTO> getAll() throws UserDisplayableException {
    ArrayList<TutoredDTO> tutoredDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_QUERY);
      ResultSet resultSet = statement.executeQuery()
    ) {
      while (resultSet.next()) {
        tutoredDTOList.add(createDTOInstanceFromResultSet(resultSet));
      }

      return tutoredDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los tutorados.");
    }
  }

  public ArrayList<TutoredDTO> getAllByTutor(int idTutor) throws UserDisplayableException {
    ArrayList<TutoredDTO> tutoredDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_TUTOR_QUERY)
    ) {
      statement.setInt(1, idTutor);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          tutoredDTOList.add(createDTOInstanceFromResultSet(resultSet));
        }
      }

      return tutoredDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los tutorados del tutor.");
    }
  }

  public ArrayList<TutoredDTO> getAllByProgram(int idProgram) throws UserDisplayableException {
    ArrayList<TutoredDTO> tutoredDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_PROGRAM_QUERY)
    ) {
      statement.setInt(1, idProgram);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          tutoredDTOList.add(createDTOInstanceFromResultSet(resultSet));
        }
      }

      return tutoredDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los tutorados del tutor.");
    }
  }

  public ArrayList<TutoredDTO> getAllByProgramAndTutor(int idProgram, int idTutor) throws UserDisplayableException {
    ArrayList<TutoredDTO> tutoredDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_PROGRAM_AND_TUTOR_QUERY)
    ) {
      statement.setInt(1, idProgram);
      statement.setInt(2, idTutor);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          tutoredDTOList.add(createDTOInstanceFromResultSet(resultSet));
        }
      }

      return tutoredDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los tutorados del tutor.");
    }
  }

  public TutoredDTO getOne(int idTutored) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ONE_QUERY)
    ) {
      statement.setInt(1, idTutored);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return createDTOInstanceFromResultSet(resultSet);
        }
      }

      throw new UserDisplayableException("El tutorado solicitado no existe.");
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar el tutorado.");
    }
  }

  public boolean updateOne(TutoredDTO tutoredDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(UPDATE_ONE_QUERY);
    ) {
      statement.setString(1, tutoredDTO.getName());
      statement.setString(2, tutoredDTO.getLastName());
      statement.setString(3, tutoredDTO.getEmail());
      statement.setString(4, tutoredDTO.getEnrollment());
      statement.setString(5, tutoredDTO.getRiskState().name());
      statement.setInt(6, tutoredDTO.getIDProgram());

      if (tutoredDTO.getIDTutor().isPresent()) {
        statement.setInt(7, tutoredDTO.getIDTutor().get());
      } else {
        statement.setNull(7, java.sql.Types.INTEGER);
      }

      statement.setInt(8, tutoredDTO.getID());
      statement.executeUpdate();

      boolean failed = statement.getUpdateCount() == -1;

      if (failed) {
        LOGGER.error(
          "Actualización Fallida Inesperada de Tutorado: {}",
          tutoredDTO.getID()
        );
      }

      return failed;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible actualizar el tutorado.");
    }
  }

  public boolean deleteOne(TutoredDTO tutoredDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(DELETE_ONE_QUERY);
    ) {
      statement.setInt(1, tutoredDTO.getID());
      statement.executeUpdate();

      boolean failed = statement.getUpdateCount() == -1;

      if (failed) {
        LOGGER.error(
          "Eliminación Fallida Inesperada de Tutorado: {}",
          tutoredDTO.getID()
        );
      }

      return failed;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible eliminar el tutorado.");
    }
  }
}
