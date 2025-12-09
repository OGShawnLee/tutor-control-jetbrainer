package daima.business.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import daima.business.dto.ProgramDTO;
import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;
import daima.common.ExceptionHandler;
import daima.common.UserDisplayableException;
import daima.db.DBConnector;

public class CatalogDAO {
  private static final Logger LOGGER = LogManager.getLogger(CatalogDAO.class);
  private static final String CREATE_COORDINATES_FOR_PROGRAM_QUERY =
    "INSERT INTO Coordinates (staff_id, program_id) VALUES (?, ?)";
  private static final String CREATE_ROLE_FOR_STAFF_QUERY = "INSERT INTO Role (role, staff_id) VALUES (?, ?)";
  private static final String CREATE_TUTORS_FOR_STAFF_QUERY = "INSERT INTO Tutors (program_id, staff_id) VALUES (?, ?)";
  private static final String GET_ALL_ROLES_FROM_STAFF_QUERY = "SELECT * FROM Role WHERE staff_id = ?";
  private static final String DELETE_ALL_COORDINATES_FROM_PROGRAM_QUERY =
    "DELETE FROM Coordinates WHERE staff_id = ? OR program_id = ?";
  private static final String DELETE_ROLE_FROM_STAFF_QUERY = "DELETE FROM Role WHERE role = ? AND staff_id = ?";
  private static final String DELETE_TUTORS_FROM_STAFF_QUERY = "DELETE FROM Tutors WHERE program_id = ? AND staff_id = ?";
  private static final CatalogDAO INSTANCE = new CatalogDAO();

  public static CatalogDAO getInstance() {
    return INSTANCE;
  }

  public ArrayList<StaffRole> getAllRolesFromStaff(int idStaff) throws UserDisplayableException {
    ArrayList<StaffRole> roles = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_ROLES_FROM_STAFF_QUERY)
    ) {
      connection.setAutoCommit(false);

      statement.setInt(1, idStaff);
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          roles.add(StaffRole.valueOf(resultSet.getString("role")));
        }
      }
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible recuperar los roles del miembro del personal");
    }

    return roles;
  }

  public void replaceAllCoordinatesForProgram(Connection connection, ProgramDTO programDTO) throws SQLException {
    if (programDTO.getIDCoordinator().isPresent()) {
      try (
        PreparedStatement deleteStatement = connection.prepareStatement(DELETE_ALL_COORDINATES_FROM_PROGRAM_QUERY);
        PreparedStatement createStatement = connection.prepareStatement(CREATE_COORDINATES_FOR_PROGRAM_QUERY)
      ) {
        connection.setAutoCommit(false);

        deleteStatement.setInt(1, programDTO.getIDCoordinator().get());
        deleteStatement.setInt(2, programDTO.getID());

        createStatement.setInt(1, programDTO.getIDCoordinator().get());
        createStatement.setInt(2, programDTO.getID());

        deleteStatement.executeUpdate();
        createStatement.executeUpdate();
      }
    }
  }

  public void createAllRolesForStaff(Connection connection, StaffDTO staffDTO) throws SQLException {
    try (
      PreparedStatement createStatement = connection.prepareStatement(CREATE_ROLE_FOR_STAFF_QUERY)
    ) {
      connection.setAutoCommit(false);

      for (StaffRole role : staffDTO.getRoles()) {
        createStatement.setString(1, role.name());
        createStatement.setInt(2, staffDTO.getID());
        createStatement.addBatch();
      }

      createStatement.executeBatch();
    }
  }

  public void replaceAllRolesForStaff(Connection connection, StaffDTO staffDTO) throws SQLException, UserDisplayableException {
    Set<StaffRole> currentRoles = new HashSet<>(getAllRolesFromStaff(staffDTO.getID()));
    Set<StaffRole> rolesForDeletion = new HashSet<>(currentRoles);
    Set<StaffRole> rolesForCreation = new HashSet<>(staffDTO.getRoles());

    rolesForDeletion.removeAll(rolesForCreation);
    rolesForCreation.removeAll(currentRoles);

    try (
      PreparedStatement deleteStatement = connection.prepareStatement(DELETE_ROLE_FROM_STAFF_QUERY);
      PreparedStatement createStatement = connection.prepareStatement(CREATE_ROLE_FOR_STAFF_QUERY)
    ) {
      connection.setAutoCommit(false);

      for (StaffRole role : rolesForDeletion) {
        deleteStatement.setString(1, role.name());
        deleteStatement.setInt(2, staffDTO.getID());
        deleteStatement.addBatch();
      }

      for (StaffRole role : rolesForCreation) {
        createStatement.setString(1, role.name());
        createStatement.setInt(2, staffDTO.getID());
        createStatement.addBatch();
      }

      deleteStatement.executeBatch();
      createStatement.executeBatch();
    }
  }

  public void createAllTutorsForStaff(Connection connection, StaffDTO staffDTO) throws SQLException {
    try (
      PreparedStatement createStatement = connection.prepareStatement(CREATE_TUTORS_FOR_STAFF_QUERY)
    ) {
      connection.setAutoCommit(false);

      for (ProgramDTO programDTO : staffDTO.getProgramTutoredList()) {
        createStatement.setInt(1, programDTO.getID());
        createStatement.setInt(2, staffDTO.getID());
        createStatement.addBatch();
      }

      createStatement.executeBatch();
    }
  }

  public void replaceAllTutorsForStaff(Connection connection, StaffDTO staffDTO) throws SQLException, UserDisplayableException {
    ArrayList<ProgramDTO> currentProgramDTOList = ProgramDAO.getInstance().getAllByTutor(staffDTO.getID());
    Set<Integer> currentProgramIDList = currentProgramDTOList.stream().map(ProgramDTO::getID).collect(Collectors.toSet());
    Set<Integer> programsForDeletion = new HashSet<>(currentProgramIDList);
    Set<Integer> programsForCreations = staffDTO.getProgramTutoredList().stream().map(ProgramDTO::getID).collect(Collectors.toSet());

    programsForDeletion.removeAll(programsForCreations);
    programsForCreations.removeAll(currentProgramIDList);

    try (
      PreparedStatement deleteStatement = connection.prepareStatement(DELETE_TUTORS_FROM_STAFF_QUERY);
      PreparedStatement createStatement = connection.prepareStatement(CREATE_TUTORS_FOR_STAFF_QUERY)
    ) {
      connection.setAutoCommit(false);

      for (Integer idProgram : programsForDeletion) {
        deleteStatement.setInt(1, idProgram);
        deleteStatement.setInt(2, staffDTO.getID());
        deleteStatement.addBatch();
      }

      for (Integer idProgram : programsForCreations) {
        createStatement.setInt(1, idProgram);
        createStatement.setInt(2, staffDTO.getID());
        createStatement.addBatch();
      }

      deleteStatement.executeBatch();
      createStatement.executeBatch();
    }
  }
}
