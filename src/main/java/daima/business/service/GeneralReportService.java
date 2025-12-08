package daima.business.service;

import daima.business.dao.ReportDAO;
import daima.business.dao.TutoringSessionPlanDAO;
import daima.business.dto.ReportDTO;
import daima.business.dto.TutoringSessionPlanDTO;
import daima.business.enumeration.ReportState;
import daima.business.enumeration.ReportType;
import daima.business.enumeration.TutoringSessionPlanState;
import daima.common.BusinessRuleException;
import daima.common.UserDisplayableException;

import java.time.LocalDate;
import java.util.AbstractMap.SimpleEntry;
import java.util.Optional;

public class GeneralReportService {
  private static final int REPORT_EDIT_GRACE_DAYS = 3;
  private static final GeneralReportService INSTANCE = new GeneralReportService();

  public static GeneralReportService getInstance() {
    return INSTANCE;
  }

  private boolean isRegistrationOrUpdateExpired(LocalDate appointmentDate) {
    LocalDate currentDate = LocalDate.now();
    LocalDate expirationDate = appointmentDate.plusDays(REPORT_EDIT_GRACE_DAYS);
    return currentDate.isAfter(expirationDate);
  }

  public SimpleEntry<TutoringSessionPlanDTO, ReportDTO> getOneForUpdate(int idReport) throws UserDisplayableException {
    ReportDTO reportDTO = ReportDAO.getInstance().getOne(idReport, ReportType.GENERAL_REPORT);

    if (reportDTO.getState() != ReportState.DRAFT) {
      throw new BusinessRuleException(
        "No es posible editar reporte general debido a que solo se pueden editar reportes que son borradores."
      );
    }

    TutoringSessionPlanDTO sessionPlanDTO = TutoringSessionPlanDAO.getInstance().getOne(reportDTO.getIDSessionPlan());

    if (isRegistrationOrUpdateExpired(sessionPlanDTO.getAppointmentDate())) {
      throw new BusinessRuleException(
        String.format(
          "No es posible editar reporte general debido a que ya han pasado %d días desde que termino su sesión de tutoría correspondiente.",
          REPORT_EDIT_GRACE_DAYS
        )
      );
    }

    return new SimpleEntry<>(sessionPlanDTO, reportDTO);
  }

  public TutoringSessionPlanDTO getLatestSessionPlanForRegistration(int idProgram) throws UserDisplayableException {
    Optional<TutoringSessionPlanDTO> latestPlanDTOOptional = TutoringSessionPlanDAO
      .getInstance()
      .findLatestByProgram(idProgram);

    if (latestPlanDTOOptional.isPresent()) {
      TutoringSessionPlanDTO latestPlanDTO = latestPlanDTOOptional.get();

      if (latestPlanDTO.getState() != TutoringSessionPlanState.COMPLETED) {
        throw new BusinessRuleException(
          "No es posible registrar reporte general debido a que aún no ha concluido la planeación de tutoría vigente."
        );
      } else {
        if (isRegistrationOrUpdateExpired(latestPlanDTO.getAppointmentDate())) {
          throw new BusinessRuleException(
            String.format(
              "No es posible registrar reporte general debido a que ya han pasado %d días desde que termino la última sesión de tutoría",
              REPORT_EDIT_GRACE_DAYS
            )
          );
        }

        Optional<ReportDTO> existingReportDTO = ReportDAO
          .getInstance()
          .findOneGeneralReportBySessionPlan(
            latestPlanDTO.getID()
          );

        if (existingReportDTO.isPresent()) {
          throw new BusinessRuleException(
            "No es posible registrar reporte general debido a que ya ha registrado uno para la sesión de tutoría actual."
          );
        }

        return latestPlanDTO;
      }
    } else {
      throw new BusinessRuleException(
        "No es posible registrar reporte general debido a que aún no hay ninguna planeación de tutoría."
      );
    }
  }

  public boolean deleteOne(ReportDTO reportDTO) throws UserDisplayableException {
    if (reportDTO.getState() != ReportState.DRAFT) {
      throw new BusinessRuleException(
        "No es posible eliminar reporte debido a que solo se pueden eliminar reportes que son borradores."
      );
    }

    return ReportDAO.getInstance().deleteOne(reportDTO);
  }

  public boolean sendOne(ReportDTO reportDTO) throws UserDisplayableException {
    if (reportDTO.getState() != ReportState.DRAFT) {
      throw new BusinessRuleException(
        "No es posible enviar reporte debido a que solo se pueden enviar reportes que son borradores."
      );
    }

    reportDTO.setState(ReportState.SENT);
    return ReportDAO.getInstance().updateOne(reportDTO);
  }
}
