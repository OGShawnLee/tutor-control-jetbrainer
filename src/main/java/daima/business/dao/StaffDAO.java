package daima.business.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;
import daima.common.ExceptionHandler;
import daima.common.InvalidFieldException;
import daima.common.UserDisplayableException;
import daima.db.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

public class StaffDAO extends DAOShape<StaffDTO> {
  private static final Logger LOGGER = LogManager.getLogger(StaffDAO.class);
  private static final String CREATE_ONE_QUERY =
    "INSERT INTO Staff (name, last_name, email, worker_id, password) VALUES (?, ?, ?, ?, ?)";
  private static final String FIND_ONE_BY_EMAIL_QUERY = "SELECT * FROM CompleteStaffView WHERE email = ?";
  private static final String FIND_ONE_BY_WORKER_ID_QUERY = "SELECT * FROM CompleteStaffView WHERE worker_id = ?";
  private static final String GET_ALL_QUERY = "SELECT * FROM CompleteStaffView ORDER BY created_at";
  private static final String GET_ALL_BY_ROLE_QUERY =
    "SELECT * FROM CompleteStaffView CT WHERE EXISTS(SELECT 1 FROM Role R WHERE R.staff_id = CT.staff_id AND R.role = ?) ORDER BY created_at";
  private static final String GET_ALL_TUTOR_BY_PROGRAM_QUERY =
    "SELECT * FROM CompleteStaffView C WHERE EXISTS(SELECT 1 FROM Role R WHERE R.staff_id = C.staff_id) AND EXISTS(SELECT 1 FROM Tutors T WHERE T.staff_id = C.staff_id AND program_id = ?) ORDER BY created_at";
  private static final String GET_ONE_QUERY = "SELECT * FROM CompleteStaffView WHERE staff_id = ?";
  private static final String UPDATE_ONE_QUERY =
    "UPDATE Staff SET name = ?, last_name = ?, email = ?, worker_id = ?, password = ? WHERE staff_id = ?";
  private static final String DELETE_ONE_QUERY = "DELETE FROM Staff WHERE staff_id = ?";
  private static final String IS_THERE_ONE_ADMIN_QUERY = "SELECT COUNT(*) FROM Role WHERE role = 'ADMIN' LIMIT 1";
  private static final StaffDAO INSTANCE = new StaffDAO();

  public static StaffDAO getInstance() {
    return INSTANCE;
  }

  @Override
  public StaffDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws InvalidFieldException, SQLException {
    StaffDTO staffDTO = new StaffDTO();
    staffDTO.setID(resultSet.getInt("staff_id"));
    staffDTO.setName(resultSet.getString("name"));
    staffDTO.setLastName(resultSet.getString("last_name"));
    staffDTO.setEmail(resultSet.getString("email"));
    staffDTO.setIDWorker(resultSet.getString("worker_id"));
    staffDTO.setEncryptedPasswordFromDB(resultSet.getString("password"));
    staffDTO.setRoles(resultSet.getString("roles"));
    staffDTO.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
    return staffDTO;
  }

  public void createOne(Connection connection, StaffDTO staffDTO) throws SQLException {
    try (
      PreparedStatement createStatement = connection.prepareStatement(CREATE_ONE_QUERY, PreparedStatement.RETURN_GENERATED_KEYS);
    ) {
      connection.setAutoCommit(false);

      createStatement.setString(1, staffDTO.getName());
      createStatement.setString(2, staffDTO.getLastName());
      createStatement.setString(3, staffDTO.getEmail());
      createStatement.setString(4, staffDTO.getIDWorker());
      createStatement.setString(5, getEncryptedPassword(staffDTO.getPassword()));
      createStatement.executeUpdate();

      try (ResultSet generatedKeys = createStatement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          staffDTO.setID(generatedKeys.getInt(1));
        } else {
          throw new SQLException("Creating user failed, no ID obtained.");
        }
      }
    }
  }

  public Optional<StaffDTO> findOneByEmail(String email) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(FIND_ONE_BY_EMAIL_QUERY)
    ) {
      statement.setString(1, email);

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

  public Optional<StaffDTO> findOneByWorkerID(String workerID) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(FIND_ONE_BY_WORKER_ID_QUERY)
    ) {
      statement.setString(1, workerID);

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

  public ArrayList<StaffDTO> getAll() throws UserDisplayableException {
    ArrayList<StaffDTO> tutoredDTOList = new ArrayList<>();

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
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los miembros del personal.");
    }
  }

  public ArrayList<StaffDTO> getAllByRole(StaffRole role) throws UserDisplayableException {
    ArrayList<StaffDTO> staffDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_ROLE_QUERY)
    ) {
      statement.setString(1, role.name());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          staffDTOList.add(createDTOInstanceFromResultSet(resultSet));
        }
      }

      return staffDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los miembros del personal.");
    }
  }

  public ArrayList<StaffDTO> getAllTutorByProgram(int idProgram) throws UserDisplayableException {
    ArrayList<StaffDTO> staffDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_TUTOR_BY_PROGRAM_QUERY)
    ) {
      statement.setInt(1, idProgram);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          staffDTOList.add(createDTOInstanceFromResultSet(resultSet));
        }
      }

      return staffDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los tutorados del tutor.");
    }
  }

  public StaffDTO getOne(int idStaff) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ONE_QUERY)
    ) {
      statement.setInt(1, idStaff);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return createDTOInstanceFromResultSet(resultSet);
        }
      }

      throw new UserDisplayableException("El miembro de personal solicitado no existe.");
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar el tutorado.");
    }
  }

  public boolean updateOne(Connection connection, StaffDTO staffDTO) throws SQLException, UserDisplayableException {
    StaffDTO oldStaffDTO = getOne(staffDTO.getID());
    try (
      PreparedStatement statement = connection.prepareStatement(UPDATE_ONE_QUERY);
    ) {
      connection.setAutoCommit(false);

      statement.setString(1, staffDTO.getName());
      statement.setString(2, staffDTO.getLastName());
      statement.setString(3, staffDTO.getEmail());
      statement.setString(4, staffDTO.getIDWorker());

      if (oldStaffDTO.getPassword().equals(staffDTO.getPassword())) {
        statement.setString(5, staffDTO.getPassword());
      } else {
        statement.setString(5, getEncryptedPassword(staffDTO.getPassword()));
      }

      statement.setInt(6, staffDTO.getID());

      statement.executeUpdate();

      boolean failed = statement.getUpdateCount() == -1;

      if (failed) {
        LOGGER.error(
          "Actualización Fallida Inesperada de Miembro de Persona: {}",
          staffDTO.getID()
        );
      }

      return failed;
    }
  }

  public void updateOne(StaffDTO staffDTO) throws UserDisplayableException {
    try (Connection connection = DBConnector.getInstance().getConnection()) {
      updateOne(connection, staffDTO);
      connection.commit();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible modificar miembro de personal");
    }
  }

  public boolean isThereAnAdminAccount() throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(IS_THERE_ONE_ADMIN_QUERY);
      ResultSet resultSet = statement.executeQuery()
    ) {
      if (resultSet.next()) {
        return resultSet.getInt(1) > 0;
      }

      return false;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible verificar la existencia de una cuenta de administrador.");
    }
  }


  public boolean deleteOne(StaffDTO staffDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(DELETE_ONE_QUERY);
    ) {
      statement.setInt(1, staffDTO.getID());
      statement.executeUpdate();

      boolean failed = statement.getUpdateCount() == -1;

      if (failed) {
        LOGGER.error(
          "Eliminación Fallida Inesperada de Miembro de Personal: {}",
          staffDTO.getID()
        );
      }

      return failed;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible eliminar miembro de personal.");
    }
  }

  private static String getEncryptedPassword(String password) {
    return BCrypt.hashpw(password, BCrypt.gensalt());
  }

  public static String createDefaultPassword(String workerID) {
    return workerID + "@Password";
  }
}
