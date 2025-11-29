package db;

import daima.db.DBConnector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

public class DBConnectorTest {
  @Test
  public void testGetConnector() {
    Assertions.assertDoesNotThrow(() -> {
      DBConnector dbConnector = DBConnector.getInstance();
      Assertions.assertNotNull(dbConnector);
    });
  }

  @Test
  public void testGetConnection() {
    Assertions.assertDoesNotThrow(() -> {
      DBConnector dbConnector = DBConnector.getInstance();
      Assertions.assertNotNull(dbConnector);

      Connection connection = dbConnector.getConnection();
      Assertions.assertNotNull(connection);
      connection.close();
    });
  }
}
