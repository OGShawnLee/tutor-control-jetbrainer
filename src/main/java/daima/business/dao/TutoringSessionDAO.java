package daima.business.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import daima.business.dto.PeriodDTO;
import daima.business.dto.TutoringSessionDTO;
import daima.business.enumeration.AppointmentState;
import daima.business.enumeration.Semester;
import daima.business.enumeration.TutoringSessionKind;
import daima.common.ExceptionHandler;
import daima.common.InvalidFieldException;
import daima.common.UserDisplayableException;
import daima.db.DBConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TutoringSessionDAO extends DAOShape<TutoringSessionDTO> {
  private static final Logger LOGGER = LogManager.getLogger(TutoringSessionDAO.class);
  private static final String CREATE_ONE_QUERY = "INSERT INTO TutoringSession (tutored_id, tutor_id, session_plan_id, hour) VALUES (?, ?, ?, ?)";
  private static final String GET_ALL_BY_TUTOR_AND_PERIOD = "SELECT * FROM CompleteTutoringSessionView WHERE tutor_id = ? AND period_year = ? AND period_semester = ? ORDER BY created_at";
  private static final String GET_ALL_BY_TUTOR = "SELECT * FROM CompleteTutoringSessionView WHERE tutor_id = ? ORDER BY created_At";
  private static final String UPDATE_ONE_QUERY = "UPDATE TutoringSession SET hour = ?, state = ? WHERE id = ?";
  private static final TutoringSessionDAO INSTANCE = new TutoringSessionDAO();

  public static TutoringSessionDAO getInstance() {
    return INSTANCE;
  }

  @Override
  public TutoringSessionDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws SQLException, InvalidFieldException {
    TutoringSessionDTO sessionDTO = new TutoringSessionDTO();
    sessionDTO.setID(resultSet.getInt("id"));
    sessionDTO.setIDTutor(resultSet.getInt("tutor_id"));
    sessionDTO.setIDTutored(resultSet.getInt("tutored_id"));
    sessionDTO.setIDPlan(resultSet.getInt("session_plan_id"));
    sessionDTO.setNameTutor(resultSet.getString("tutor_name"));
    sessionDTO.setNameTutored(resultSet.getString("tutored_name"));
    sessionDTO.setEnrollment(resultSet.getString("tutored_enrollment"));
    sessionDTO.setHour(resultSet.getString("hour"));
    sessionDTO.setKind(TutoringSessionKind.valueOf(resultSet.getString("session_kind")));
    sessionDTO.setState(AppointmentState.valueOf(resultSet.getString("state")));
    sessionDTO.setAppointmentDate(resultSet.getDate("appointment_date").toLocalDate());
    sessionDTO.setPeriodDTO(
      new PeriodDTO(
        resultSet.getInt("period_year"),
        Semester.valueOf(resultSet.getString("period_semester"))
      )
    );
    sessionDTO.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
    return sessionDTO;
  }

  public void createOne(TutoringSessionDTO sessionDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(CREATE_ONE_QUERY)
    ) {
      statement.setInt(1, sessionDTO.getIDTutored());
      statement.setInt(2, sessionDTO.getIDTutor());
      statement.setInt(3, sessionDTO.getIDPlan());
      statement.setString(4, sessionDTO.getHour());

      statement.executeUpdate();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear la sesión de tutoría.");
    }
  }

  public ArrayList<TutoringSessionDTO> getAllByTutorAndPeriod(int idTutor, PeriodDTO periodDTO) throws UserDisplayableException {
    ArrayList<TutoringSessionDTO> sessionDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_TUTOR_AND_PERIOD)
    ) {
      statement.setInt(1, idTutor);
      statement.setInt(2, periodDTO.getYear());
      statement.setString(3, periodDTO.getSemester().name());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          sessionDTOList.add(createDTOInstanceFromResultSet(resultSet));
        }
      }

      return sessionDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar las sesiones de tutoría.");
    }
  }

  public ArrayList<TutoringSessionDTO> getAllByTutor(int idTutor) throws UserDisplayableException {
    ArrayList<TutoringSessionDTO> sessionDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_TUTOR)
    ) {
      statement.setInt(1, idTutor);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          sessionDTOList.add(createDTOInstanceFromResultSet(resultSet));
        }
      }

      return sessionDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar las sesiones de tutoría.");
    }
  }

  public boolean updateOne(TutoringSessionDTO sessionDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(UPDATE_ONE_QUERY)
    ) {
      statement.setString(1, sessionDTO.getHour());
      statement.setString(2, sessionDTO.getState().name());
      statement.setInt(3, sessionDTO.getID());

      statement.executeUpdate();

      boolean failed = statement.getUpdateCount() == -1;

      if (failed) {
        LOGGER.error(
          "Actualización Fallida Inesperada de Sesión de Tutoría: {}",
          sessionDTO.getID()
        );
      }

      return failed;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible modificar la sesión de tutoría.");
    }
  }
}
