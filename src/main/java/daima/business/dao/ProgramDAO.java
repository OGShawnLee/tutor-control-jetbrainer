package daima.business.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import daima.business.dto.ProgramDTO;
import daima.common.ExceptionHandler;
import daima.common.UserDisplayableException;
import daima.db.DBConnector;

public class ProgramDAO extends DAOShape<ProgramDTO> {
  private static final Logger LOGGER = LogManager.getLogger(ProgramDAO.class);
  private static final String CREATE_ONE_QUERY = "INSERT INTO Program (name, acronym) VALUES (?, ?)";
  private static final String GET_ALL_QUERY = "SELECT * FROM CompleteProgramView ORDER BY created_at";
  private static final String GET_ALL_BY_TUTOR_QUERY =
    "SELECT * FROM CompleteProgramView CP WHERE EXISTS(SELECT 1 FROM Tutors T WHERE T.program_id = CP.program_id AND T.staff_id = ?)";
  private static final String GET_ALL_WITH_NO_COORDINATOR_QUERY = "SELECT * FROM CompleteProgramView WHERE coordinator_id IS NULL ORDER BY created_at";
  private static final String GET_ONE_QUERY = "SELECT * FROM CompleteProgramView WHERE program_id = ?";
  private static final String GET_ONE_BY_COORDINATOR = "SELECT * FROM CompleteProgramView WHERE coordinator_id = ?";
  private static final String FIND_ONE_BY_ACRONYM_QUERY = "SELECT * FROM CompleteProgramView WHERE acronym = ?";
  private static final String UPDATE_ONE_QUERY = "UPDATE Program SET name = ?, acronym = ? WHERE program_id = ?";
  private static final ProgramDAO INSTANCE = new ProgramDAO();

  public static ProgramDAO getInstance() {
    return INSTANCE;
  }

  @Override
  public ProgramDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws SQLException {
    ProgramDTO programDTO = new ProgramDTO();
    programDTO.setID(resultSet.getInt("program_id"));
    programDTO.setName(resultSet.getString("name"));
    programDTO.setAcronym(resultSet.getString("acronym"));
    programDTO.setIDCoordinator(resultSet.getInt("coordinator_id"));
    programDTO.setNameCoordinator(resultSet.getString("name_coordinator"));
    programDTO.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
    return programDTO;
  }

  public void createOne(Connection connection, ProgramDTO programDTO) throws SQLException {
    try (
      PreparedStatement statement = connection.prepareStatement(CREATE_ONE_QUERY, PreparedStatement.RETURN_GENERATED_KEYS)
    ) {
      connection.setAutoCommit(false);

      statement.setString(1, programDTO.getName());
      statement.setString(2, programDTO.getAcronym());
      statement.executeUpdate();

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          programDTO.setID(generatedKeys.getInt(1));
        } else {
          throw new SQLException("Creating user failed, no ID obtained.");
        }
      }
    }
  }

  public Optional<ProgramDTO> findOneByAcronym(String acronym) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(FIND_ONE_BY_ACRONYM_QUERY)
    ) {
      statement.setString(1, acronym);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return Optional.of(createDTOInstanceFromResultSet(resultSet));
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar el miembro de personal.");
    }
  }

  public ArrayList<ProgramDTO> getAll() throws UserDisplayableException {
    ArrayList<ProgramDTO> programDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_QUERY);
      ResultSet resultSet = statement.executeQuery()
    ) {
      while (resultSet.next()) {
        programDTOList.add(createDTOInstanceFromResultSet(resultSet));
      }

      return programDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los programas.");
    }
  }

  public ArrayList<ProgramDTO> getAllByTutor(int idTutor) throws UserDisplayableException {
    ArrayList<ProgramDTO> programDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_TUTOR_QUERY);
    ) {

      statement.setInt(1, idTutor);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          programDTOList.add(createDTOInstanceFromResultSet(resultSet));
        }
      }

      return programDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los programas.");
    }
  }

  public ArrayList<ProgramDTO> getAllWithNoCoordinator() throws UserDisplayableException {
    ArrayList<ProgramDTO> programDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_WITH_NO_COORDINATOR_QUERY);
      ResultSet resultSet = statement.executeQuery()
    ) {
      while (resultSet.next()) {
        programDTOList.add(createDTOInstanceFromResultSet(resultSet));
      }

      return programDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los programas.");
    }
  }

  public ProgramDTO getOne(int idProgram) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ONE_QUERY);
    ) {

      statement.setInt(1, idProgram);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return createDTOInstanceFromResultSet(resultSet);
        }
      }

      LOGGER.warn("No se ha encontrado el Programa con ID: {}", idProgram);

      return null;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar el programa.");
    }
  }

  public ProgramDTO getOneByCoordinator(int idCoordinator) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ONE_BY_COORDINATOR);
    ) {

      statement.setInt(1, idCoordinator);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return createDTOInstanceFromResultSet(resultSet);
        }
      }

      return null;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar el programa.");
    }
  }

  public boolean updateOne(Connection connection, ProgramDTO programDTO) throws SQLException {
    try (
      PreparedStatement statement = connection.prepareStatement(UPDATE_ONE_QUERY)
    ) {
      connection.setAutoCommit(false);

      statement.setString(1, programDTO.getName());
      statement.setString(2, programDTO.getAcronym());
      statement.setInt(3, programDTO.getID());
      statement.executeUpdate();

      boolean failed = statement.getUpdateCount() == -1;

      if (failed) {
        LOGGER.error(
          "Actualización Fallida Inesperada de Programa: {}",
          String.format("%s %s", programDTO.getID(), programDTO.getName())
        );
      }

      return failed;
    }
  }
}
