package daima.business.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import daima.business.dao.TutoredDAO;
import daima.business.dao.TutoringSessionDAO;
import daima.business.dao.TutoringSessionPlanDAO;
import daima.business.dto.TutoredDTO;
import daima.business.dto.TutoringSessionDTO;
import daima.business.dto.TutoringSessionPlanDTO;
import daima.business.enumeration.AppointmentState;
import daima.business.enumeration.TutoringSessionPlanState;
import daima.common.BusinessRuleException;
import daima.common.UserDisplayableException;

public class TutoringSessionService {
  private static final TutoringSessionService INSTANCE = new TutoringSessionService();

  public static TutoringSessionService getInstance() {
    return INSTANCE;
  }

  public boolean confirmAttendance(TutoringSessionDTO sessionDTO) throws UserDisplayableException {
    if (sessionDTO.getState() != AppointmentState.SCHEDULED) {
      throw new BusinessRuleException(
        "No es posible confirmar asistencia debido a que solo se pueden confirmar sesiones que estan programadas."
      );
    }

    LocalDate now = LocalDate.now();

    if (now.isEqual(sessionDTO.getAppointmentDate())) {
      sessionDTO.setState(AppointmentState.COMPLETED);
      return TutoringSessionDAO.getInstance().updateOne(sessionDTO);
    }

    throw new BusinessRuleException(
      "No es posible confirmar asistencia debido a que solo se pueden confirmar durante el día de la sesión de tutoría."
    );
  }

  public void handleCanUpdateTutoringSessionVerification(TutoringSessionDTO sessionDTO) throws BusinessRuleException {
    if (sessionDTO.getState() != AppointmentState.SCHEDULED) {
      throw new BusinessRuleException("No es posible reagendar horario debido a que solo se puede reagendar un horario cuando esta programado.");
    }
  }

  public ArrayList<TutoredDTO> getTutoredListForRegistration(int idTutor, int idProgram, int idTutoringSession) throws UserDisplayableException {
    ArrayList<TutoredDTO> tutoredDTOList = TutoredDAO.getInstance().getAllByProgramAndTutorWithNoTutoringSession(
      idProgram,
      idTutor,
      idTutoringSession
    );

    if (tutoredDTOList.isEmpty()) {
      throw new BusinessRuleException(
        "No es posible agendar horario porque no hay ningún tutorado pendiente de agendar."
      );
    }

    return tutoredDTOList;
  }

  public TutoringSessionPlanDTO getLatestSessionPlanForRegistration(int idProgram) throws UserDisplayableException {
    Optional<TutoringSessionPlanDTO> previousPlanDTO = TutoringSessionPlanDAO
      .getInstance()
      .findLatestByProgram(idProgram);

    if (previousPlanDTO.isPresent()) {
      if (previousPlanDTO.get().getState() == TutoringSessionPlanState.COMPLETED) {
        throw new BusinessRuleException(
          "No es posible agendar un horario debido a que aún no hay ninguna planeación de tutoría vigente para el programa educativo seleccionado."
        );
      } else {
        return previousPlanDTO.get();
      }
    } else {
      throw new BusinessRuleException(
        "No es posible agendar horario de tutoría debido a que aún no hay ninguna planeación de tutoría para el programa educativo seleccionado."
      );
    }
  }
}