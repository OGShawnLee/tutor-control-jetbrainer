package daima.business.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import daima.business.dto.ProgramDTO;
import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;

public class CatalogDAO {
  private static final String CREATE_COORDINATES_FOR_PROGRAM_QUERY = "INSERT INTO Coordinates (staff_id, program_id) VALUES (?, ?)";
  private static final String CREATE_ROLE_FOR_STAFF_QUERY = "INSERT INTO role (staff_id, role) VALUES (?, ?)";
  private static final String CREATE_TUTORS_FOR_STAFF_QUERY = "INSERT INTO Tutors (staff_id, program_id) VALUES (?, ?)";
  private static final String GET_ALL_ROLES_FROM_STAFF_QUERY = "SELECT * FROM role WHERE staff_id = ?";
  private static final String DELETE_ALL_COORDINATES_FROM_PROGRAM_QUERY = "DELETE FROM Coordinates WHERE staff_id = ? OR program_id = ?";
  private static final String DELETE_ALL_ROLES_FROM_STAFF_QUERY = "DELETE FROM role WHERE staff_id = ?";
  private static final String DELETE_ALL_TUTORS_FROM_STAFF_QUERY = "DELETE FROM Tutors WHERE program_id = ?";
  private static final CatalogDAO INSTANCE = new CatalogDAO();

  public static CatalogDAO getInstance() {
    return INSTANCE;
  }

  public ArrayList<StaffRole> getAllRolesFromStaff(Connection connection, StaffDTO staffDTO) throws SQLException {
    ArrayList<StaffRole> roles = new ArrayList<>();

    try (PreparedStatement statement = connection.prepareStatement(GET_ALL_ROLES_FROM_STAFF_QUERY)) {
      connection.setAutoCommit(false);

      statement.setInt(1, staffDTO.getID());
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          roles.add(StaffRole.valueOf(resultSet.getString("role")));
        }
      }
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

    // TODO: HANDLE ILLEGAL ARGUMENT EXCEPTION
  }

  public void replaceAllRolesForStaff(Connection connection, StaffDTO staffDTO) throws SQLException {
    try (
      PreparedStatement deleteStatement = connection.prepareStatement(DELETE_ALL_ROLES_FROM_STAFF_QUERY);
      PreparedStatement createStatement = connection.prepareStatement(CREATE_ROLE_FOR_STAFF_QUERY)
    ) {
      connection.setAutoCommit(false);

      deleteStatement.setInt(1, staffDTO.getID());

      for (StaffRole role : staffDTO.getRoles()) {
        createStatement.setInt(1, staffDTO.getID());
        createStatement.setString(2, role.name());
        createStatement.addBatch();
      }

      deleteStatement.executeUpdate();
      createStatement.executeBatch();
    }
  }

  public void replaceAllTutorsForStaff(Connection connection, StaffDTO staffDTO) throws SQLException {
    try (
      PreparedStatement deleteStatement = connection.prepareStatement(DELETE_ALL_TUTORS_FROM_STAFF_QUERY);
      PreparedStatement createStatement = connection.prepareStatement(CREATE_TUTORS_FOR_STAFF_QUERY)
    ) {
      connection.setAutoCommit(false);

      deleteStatement.setInt(1, staffDTO.getID());

      for (ProgramDTO programDTO : staffDTO.getProgramTutoredList()) {
        createStatement.setInt(1, staffDTO.getID());
        createStatement.setInt(2, programDTO.getID());
        createStatement.addBatch();
      }

      deleteStatement.executeUpdate();
      createStatement.executeBatch();
    }
  }
}
