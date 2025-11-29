package daima.business.dao;

import daima.business.dto.ReportDTO;
import daima.business.enumeration.ReportType;

import java.util.ArrayList;
import java.util.Arrays;

public class ReportDAO {
  private final static ReportDAO INSTANCE = new ReportDAO();

  public static ReportDAO getInstance() {
    return INSTANCE;
  }

  public ArrayList<ReportDTO> getAllByProgram(int idProgram, ReportType type) {
    return new ArrayList<>(Arrays.asList(
      new ReportDTO(),
      new ReportDTO(),
      new ReportDTO(),
      new ReportDTO(),
      new ReportDTO()
    ));
  }
}
