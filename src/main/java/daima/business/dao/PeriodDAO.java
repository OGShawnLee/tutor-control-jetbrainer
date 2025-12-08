package daima.business.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import daima.business.dto.PeriodDTO;
import daima.business.enumeration.Semester;
import daima.common.ExceptionHandler;
import daima.common.UserDisplayableException;
import daima.db.DBConnector;

public class PeriodDAO extends DAOShape<PeriodDTO> {
  private static final Logger LOGGER = LogManager.getLogger(PeriodDAO.class);
  private static final PeriodDAO INSTANCE = new PeriodDAO();
  private static final String GET_ALL_QUERY = "SELECT * FROM Period ORDER BY year DESC, semester DESC";
  private static final String GET_CURRENT_PERIOD_QUERY = "CALL get_or_create_latest_period()";

  public static PeriodDAO getInstance() {
    return INSTANCE;
  }

  @Override
  public PeriodDTO getDTOInstanceFromResultSet(ResultSet resultSet) throws SQLException {
    return new PeriodDTO(
      resultSet.getInt("year"),
      Semester.valueOf(resultSet.getString("semester"))
    );
  }

  public ArrayList<PeriodDTO> getAll() throws UserDisplayableException {
    ArrayList<PeriodDTO> periodList = new ArrayList<>();

    try (
      Connection connection = DBConnector.getInstance().getConnection();
      CallableStatement statement = connection.prepareCall(GET_ALL_QUERY);
      ResultSet resultSet = statement.executeQuery()
    ) {
      while (resultSet.next()) {
        periodList.add(createDTOInstanceFromResultSet(resultSet));
      }
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible obtener los períodos.");
    }

    return periodList;
  }

  public PeriodDTO getCurrentPeriod() throws UserDisplayableException {
    final String GET_CURRENT_PERIOD_QUERY = "CALL get_or_create_latest_period()";
    try (
      Connection connection = DBConnector.getInstance().getConnection();
      CallableStatement statement = connection.prepareCall(GET_CURRENT_PERIOD_QUERY);
      ResultSet resultSet = statement.executeQuery()
    ) {
      if (resultSet.next()) {
        return createDTOInstanceFromResultSet(resultSet);
      }

      throw new UserDisplayableException("No se ha podido obtener el período actual.");
    } catch (SQLException e) {
      throw ExceptionHandler.handleSQLException(LOGGER, e, "No ha sido posible obtener el período actual.");
    }
  }
}
