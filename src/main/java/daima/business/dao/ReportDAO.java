package daima.business.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import daima.business.dto.PeriodDTO;
import daima.business.dto.ReportDTO;
import daima.business.enumeration.ReportState;
import daima.business.enumeration.ReportType;
import daima.business.enumeration.Semester;
import daima.business.enumeration.TutoringSessionKind;
import daima.common.ExceptionHandler;
import daima.common.UserDisplayableException;
import daima.db.DBConnector;

public class ReportDAO extends DAOShape<ReportDTO> {
  private static final Logger LOGGER = LogManager.getLogger(ReportDAO.class);
  private static final String CREATE_ONE_QUERY =
    "INSERT INTO Report (staff_id, session_plan_id, content, type) VALUES (?, ?, ?, ?)";
  private static final String FIND_ONE_GENERAL_REPORT_BY_SESSION_PLAN_QUERY =
    "SELECT * FROM CompleteReportView WHERE type = 'GENERAL_REPORT' AND session_plan_id = ?";
  private static final String GET_ALL_QUERY = "SELECT * FROM CompleteReportView WHERE type = ? ORDER BY created_at";
  private static final String GET_ALL_BY_PROGRAM_QUERY =
    "SELECT * FROM CompleteReportView WHERE type = ? AND program_id = ? ORDER BY created_at";
  private static final String GET_ONE_QUERY = "SELECT * FROM CompleteReportView WHERE id = ? AND type = ?";
  private static final String UPDATE_ONE_QUERY = "UPDATE Report SET content = ?, state = ? WHERE id = ?";
  private static final String DELETE_ONE_QUERY = "DELETE FROM Report WHERE id = ?";
  private static final ReportDAO INSTANCE = new ReportDAO();

  public static ReportDAO getInstance() {
    return INSTANCE;
  }

  @Override
  public ReportDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws SQLException {
    ReportDTO reportDTO = new ReportDTO();
    reportDTO.setID(resultSet.getInt("id"));
    reportDTO.setIDStaff(resultSet.getInt("staff_id"));
    reportDTO.setIDSessionPlan(resultSet.getInt("session_plan_id"));
    reportDTO.setIDProgram(resultSet.getInt("program_id"));
    reportDTO.setNameStaff(resultSet.getString("name_staff"));
    reportDTO.setContent(resultSet.getString("content"));
    reportDTO.setSessionKind(
      TutoringSessionKind.valueOf(resultSet.getString("session_kind"))
    );
    reportDTO.setType(ReportType.valueOf(resultSet.getString("type")));
    reportDTO.setState(ReportState.valueOf(resultSet.getString("state")));
    reportDTO.setPeriodDTO(
      new PeriodDTO(
        resultSet.getInt("period_year"),
        Semester.valueOf(resultSet.getString("period_semester"))
      )
    );
    reportDTO.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
    return reportDTO;
  }

  public void createOne(ReportDTO reportDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(CREATE_ONE_QUERY)
    ) {
      statement.setInt(1, reportDTO.getIDStaff());
      statement.setInt(2, reportDTO.getIDSessionPlan());
      statement.setString(3, reportDTO.getContent());
      statement.setString(4, reportDTO.getType().name());

      statement.executeUpdate();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear el reporte.");
    }
  }

  public Optional<ReportDTO> findOneGeneralReportBySessionPlan(int idSessionPlan) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(FIND_ONE_GENERAL_REPORT_BY_SESSION_PLAN_QUERY)
    ) {
      statement.setInt(1, idSessionPlan);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return Optional.of(createDTOInstanceFromResultSet(resultSet));
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar el reporte.");
    }
  }

  public ArrayList<ReportDTO> getAll(ReportType type) throws UserDisplayableException {
    ArrayList<ReportDTO> reportDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_QUERY)
    ) {
      statement.setString(1, type.name());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          reportDTOList.add(createDTOInstanceFromResultSet(resultSet));
        }
      }

      return reportDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los reportes.");
    }
  }

  public ArrayList<ReportDTO> getAllByProgram(int idProgram, ReportType type) throws UserDisplayableException {
    ArrayList<ReportDTO> reportDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_PROGRAM_QUERY)
    ) {
      statement.setString(1, type.name());
      statement.setInt(2, idProgram);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          reportDTOList.add(createDTOInstanceFromResultSet(resultSet));
        }
      }

      return reportDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los reportes.");
    }
  }

  public ReportDTO getOne(int idReport, ReportType type) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ONE_QUERY)
    ) {
      statement.setInt(1, idReport);
      statement.setString(2, type.name());

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return createDTOInstanceFromResultSet(resultSet);
        }
      }

      throw new UserDisplayableException("El reporte solicitado no existe.");
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar el reporte.");
    }
  }

  public boolean updateOne(ReportDTO reportDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(UPDATE_ONE_QUERY);
    ) {
      statement.setString(1, reportDTO.getContent());
      statement.setString(2, reportDTO.getState().name());
      statement.setInt(3, reportDTO.getID());

      statement.executeUpdate();

      boolean failed = statement.getUpdateCount() == -1;

      if (failed) {
        LOGGER.error(
          "Actualización Fallida Inesperada de Reporte: {} {}",
          reportDTO.getID(),
          reportDTO.getType().name()
        );
      }

      return failed;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible actualizar el reporte.");
    }
  }

  public boolean deleteOne(ReportDTO reportDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(DELETE_ONE_QUERY);
    ) {
      statement.setInt(1, reportDTO.getID());
      statement.executeUpdate();

      boolean failed = statement.getUpdateCount() == -1;

      if (failed) {
        LOGGER.error(
          "Eliminación Fallida Inesperada de Reporte: {} {}",
          reportDTO.getID(),
          reportDTO.getType().name()
        );
      }

      return failed;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible eliminar el reporte.");
    }
  }
}
