package daima.business.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import daima.business.dto.PeriodDTO;
import daima.business.dto.TutoringSessionPlanDTO;
import daima.business.enumeration.Semester;
import daima.business.enumeration.TutoringSessionKind;
import daima.business.enumeration.TutoringSessionPlanState;
import daima.common.ExceptionHandler;
import daima.common.UserDisplayableException;
import daima.db.DBConnector;

public class TutoringSessionPlanDAO extends DAOShape<TutoringSessionPlanDTO> {
  private static final Logger LOGGER = LogManager.getLogger(TutoringSessionPlanDAO.class);
  private static final String CREATE_ONE_QUERY
    = "INSERT INTO TutoringSessionPlan (program_id, period_year, period_semester, kind, appointment_date) VALUES (?, ?, ?, ?, ?)";
  private static final String FIND_LATEST_BY_PROGRAM_QUERY
    = "SELECT * FROM CompleteTutoringSessionPlanView WHERE program_id = ? ORDER BY created_at DESC LIMIT 1";
  private static final String GET_ALL_BY_PROGRAM_QUERY = "SELECT * FROM CompleteTutoringSessionPlanView WHERE program_id = ? ORDER BY created_at";
  private static final String GET_ALL_BY_PROGRAM_AND_PERIOD_QUERY
    = "SELECT * FROM CompleteTutoringSessionPlanView WHERE program_id = ? AND period_year = ? AND period_semester = ? ORDER BY created_at";
  private static final String GET_ONE_QUERY = "SELECT * FROM CompleteTutoringSessionPlanView WHERE id = ?";
  private static final TutoringSessionPlanDAO INSTANCE = new TutoringSessionPlanDAO();

  public static TutoringSessionPlanDAO getInstance() {
    return INSTANCE;
  }

  @Override
  public TutoringSessionPlanDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws SQLException {
    TutoringSessionPlanDTO planDTO = new TutoringSessionPlanDTO();
    planDTO.setID(resultSet.getInt("id"));
    planDTO.setIDProgram(resultSet.getInt("program_id"));
    planDTO.setPeriodDTO(
      new PeriodDTO(
        resultSet.getInt("period_year"),
        Semester.valueOf(resultSet.getString("period_semester"))
      )
    );
    planDTO.setState(TutoringSessionPlanState.valueOf(resultSet.getString("state")));
    planDTO.setKind(TutoringSessionKind.valueOf(resultSet.getString("kind")));
    planDTO.setAppointmentDate(resultSet.getDate("appointment_date").toLocalDate());
    planDTO.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
    return planDTO;
  }

  public void createOne(TutoringSessionPlanDTO planDTO) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(CREATE_ONE_QUERY)
    ) {
      statement.setInt(1, planDTO.getIDProgram());
      statement.setInt(2, planDTO.getPeriodDTO().getYear());
      statement.setString(3, planDTO.getPeriodDTO().getSemester().name());
      statement.setString(4, planDTO.getKind().name());
      statement.setDate(5, Date.valueOf(planDTO.getAppointmentDate()));

      statement.executeUpdate();
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible crear el tutorado.");
    }
  }

  public ArrayList<TutoringSessionPlanDTO> getAllByProgram(int idProgram) throws UserDisplayableException {
    ArrayList<TutoringSessionPlanDTO> staffDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_PROGRAM_QUERY)
    ) {
      statement.setInt(1, idProgram);

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          staffDTOList.add(getDTOInstanceFromResultSet(resultSet));
        }
      }

      return staffDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los tutorados del tutor.");
    }
  }

  public ArrayList<TutoringSessionPlanDTO> getAllByProgramAndPeriod(int idProgram, PeriodDTO periodDTO) throws UserDisplayableException {
    ArrayList<TutoringSessionPlanDTO> staffDTOList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ALL_BY_PROGRAM_AND_PERIOD_QUERY)
    ) {
      statement.setInt(1, idProgram);
      statement.setInt(2, periodDTO.getYear());
      statement.setString(3, periodDTO.getSemester().name());

      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          staffDTOList.add(getDTOInstanceFromResultSet(resultSet));
        }
      }

      return staffDTOList;
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar los tutorados del tutor.");
    }
  }

  public TutoringSessionPlanDTO getOne(int idPlan) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      PreparedStatement statement = connection.prepareStatement(GET_ONE_QUERY)
    ) {
      statement.setInt(1, idPlan);

      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return createDTOInstanceFromResultSet(resultSet);
        }
      }

      throw new UserDisplayableException("La planeación de tutoría solicitada no existe.");
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible cargar la planeación de tutoría.");
    }
  }

  public Optional<TutoringSessionPlanDTO> findLatestByProgram(int idProgram) throws UserDisplayableException {
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      CallableStatement statement = connection.prepareCall(FIND_LATEST_BY_PROGRAM_QUERY)
    ) {
      statement.setInt(1, idProgram);

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
}
