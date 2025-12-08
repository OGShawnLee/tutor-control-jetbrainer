package business.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import daima.business.dao.PeriodDAO;
import daima.business.dto.PeriodDTO;

public class PeriodDAOTest {
  @Test
  public void testGetLatestPeriod() {
    Assertions.assertDoesNotThrow(() -> {
      PeriodDTO periodDTO = PeriodDAO.getInstance().getCurrentPeriod();
      Assertions.assertNotNull(periodDTO);
    });
  }
}
