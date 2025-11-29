package daima.business.dao;

import daima.business.dto.TutoringSessionPlanDTO;
import daima.business.enumeration.TutoringSessionKind;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class TutoringSessionPlanDAO {
  private final static TutoringSessionPlanDAO INSTANCE = new TutoringSessionPlanDAO();

  public static TutoringSessionPlanDAO getInstance() {
    return INSTANCE;
  }

  public ArrayList<TutoringSessionPlanDTO> getAllByProgram(int idProgram) {
    return new ArrayList<>(Arrays.asList(
      new TutoringSessionPlanDTO(LocalDate.of(2023, 4, 5), TutoringSessionKind.FIRST_TUTORING_SESSION, 0, 0),
      new TutoringSessionPlanDTO(LocalDate.of(2023, 5, 5), TutoringSessionKind.SECOND_TUTORING_SESSION, 0, 0),
      new TutoringSessionPlanDTO(LocalDate.of(2023, 6, 5), TutoringSessionKind.THIRD_TUTORING_SESSION, 0, 0),
      new TutoringSessionPlanDTO(LocalDate.of(2024, 7, 5), TutoringSessionKind.FIRST_TUTORING_SESSION, 0, 0),
      new TutoringSessionPlanDTO(LocalDate.of(2024, 8, 5), TutoringSessionKind.SECOND_TUTORING_SESSION, 0, 0),
      new TutoringSessionPlanDTO(LocalDate.of(2024, 9, 5), TutoringSessionKind.THIRD_TUTORING_SESSION, 0, 0),
      new TutoringSessionPlanDTO(LocalDate.of(2025, 10, 5), TutoringSessionKind.FIRST_TUTORING_SESSION, 0, 0),
      new TutoringSessionPlanDTO(LocalDate.of(2025, 11, 5), TutoringSessionKind.SECOND_TUTORING_SESSION, 0, 0),
      new TutoringSessionPlanDTO(LocalDate.of(2025, 12, 5), TutoringSessionKind.THIRD_TUTORING_SESSION, 0, 0)
    ));
  }
}
