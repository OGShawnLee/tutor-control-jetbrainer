package daima.business.dao;

import daima.business.dto.TutoringSessionDTO;
import daima.business.enumeration.AppointmentState;
import daima.business.enumeration.TutoringSessionKind;

import java.util.ArrayList;
import java.util.Arrays;

public class TutoringSessionDAO {
  private final static TutoringSessionDAO INSTANCE = new TutoringSessionDAO();

  public static TutoringSessionDAO getInstance() {
    return INSTANCE;
  }

  public ArrayList<TutoringSessionDTO> getAllByTutor(int idTutor) {
    return new ArrayList<>(Arrays.asList(
      new TutoringSessionDTO(AppointmentState.COMPLETED, TutoringSessionKind.FIRST_TUTORING_SESSION),
      new TutoringSessionDTO(AppointmentState.COMPLETED, TutoringSessionKind.FIRST_TUTORING_SESSION),
      new TutoringSessionDTO(AppointmentState.COMPLETED, TutoringSessionKind.FIRST_TUTORING_SESSION),
      new TutoringSessionDTO(AppointmentState.COMPLETED, TutoringSessionKind.FIRST_TUTORING_SESSION),
      new TutoringSessionDTO(AppointmentState.COMPLETED, TutoringSessionKind.FIRST_TUTORING_SESSION),
      new TutoringSessionDTO(AppointmentState.COMPLETED, TutoringSessionKind.FIRST_TUTORING_SESSION)
    ));
  }
}
