package daima.business.service;

import java.util.Optional;

import daima.business.dao.PeriodDAO;
import daima.business.dao.TutoringSessionPlanDAO;
import daima.business.dto.PeriodDTO;
import daima.business.dto.TutoringSessionPlanDTO;
import daima.business.enumeration.TutoringSessionKind;
import daima.business.enumeration.TutoringSessionPlanState;
import daima.common.BusinessRuleException;
import daima.common.UserDisplayableException;

public class TutoringSessionPlanService {
  private static final TutoringSessionPlanService INSTANCE = new TutoringSessionPlanService();

  public static TutoringSessionPlanService getInstance() {
    return INSTANCE;
  }

  public TutoringSessionKind getCurrentSessionKindForRegistration(
    int idProgram
  ) throws UserDisplayableException {
    Optional<TutoringSessionPlanDTO> previousPlanDTO = TutoringSessionPlanDAO
      .getInstance()
      .findLatestByProgram(idProgram);

    if (previousPlanDTO.isPresent()) {
      if (previousPlanDTO.get().getState() == TutoringSessionPlanState.SCHEDULED) {
        throw new BusinessRuleException(
          "No es posible registrar una nueva planeación de tutoría mientras haya una planeación de tutoría vigente."
        );
      }

      TutoringSessionKind previousKind = previousPlanDTO.get().getKind();

      switch (previousKind) {
        case FIRST_TUTORING_SESSION:
          return TutoringSessionKind.SECOND_TUTORING_SESSION;
        case SECOND_TUTORING_SESSION:
          return TutoringSessionKind.THIRD_TUTORING_SESSION;
        case THIRD_TUTORING_SESSION: {
          PeriodDTO periodDTO = previousPlanDTO.get().getPeriodDTO();
          PeriodDTO currentPeriodDTO = PeriodDAO.getInstance().getCurrentPeriod();

          if (periodDTO.toString().equals(currentPeriodDTO.toString())) {
            throw new BusinessRuleException(
              "No es posible registrar una nueva planeación de tutoría hasta que comience el nuevo periodo educativo."
            );
          }

          return TutoringSessionKind.FIRST_TUTORING_SESSION;
        }
        default:
          throw new IllegalStateException(
            String.format(
              "Tipo de sesión de tutoría inesperado o no manejado: %s Revise la lógica del enum o la base de datos.",
              previousKind
            )
          );
      }
    } else {
      return TutoringSessionKind.FIRST_TUTORING_SESSION;
    }
  }
}
