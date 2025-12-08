package daima.business.service;

import java.util.Optional;

import daima.business.dao.TutoringSessionDAO;
import daima.business.dao.TutoringSessionPlanDAO;
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
      throw new BusinessRuleException("No es posible confirmar asistencia debido a que solo se pueden confirmar sesiones que estan programadas.");
    }

    sessionDTO.setState(AppointmentState.COMPLETED);
    return TutoringSessionDAO.getInstance().updateOne(sessionDTO);
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