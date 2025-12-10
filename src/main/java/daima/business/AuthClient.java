package daima.business;

import daima.business.dao.ProgramDAO;
import daima.business.dto.ProgramDTO;
import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;
import daima.common.BusinessRuleException;
import daima.common.UserDisplayableException;

import java.util.ArrayList;

/*
 * AuthClient is a singleton class that manages the authentication state of the application.
 * It holds information about the currently logged-in user and their role.
 */
public class AuthClient {
  private StaffDTO currentStaff;
  private StaffRole currentRole;
  private static AuthClient instance;

  private AuthClient() {
    this.currentStaff = null;
  }

  public static AuthClient getInstance() {
    if (instance == null) {
      instance = new AuthClient();
    }

    return instance;
  }

  public void handleSignIn(StaffDTO staffDTO, StaffRole role) throws UserDisplayableException {
    this.currentStaff = staffDTO;
    this.currentRole = role;

    if (role == StaffRole.COORDINATOR) {
      try {
        ProgramDTO programDTO = ProgramDAO.getInstance().getOneByCoordinator(staffDTO.getID());
        this.currentStaff.setCoordinatedProgram(programDTO);
      } catch (UserDisplayableException e) {
        throw new UserDisplayableException(
          "No ha sido posible obtener el programa educativo asociado al coordinador. Si el problema persiste, contacte a un administrador."
        );
      }
    }

    if (role == StaffRole.TUTOR) {
      try {
        ArrayList<ProgramDTO> programDTOList = ProgramDAO.getInstance().getAllByTutor(staffDTO.getID());
        staffDTO.setProgramTutoredList(programDTOList);
      } catch (UserDisplayableException e) {
        throw new UserDisplayableException("No ha sido posible obtener los programas educativos asociados al tutor.");
      }
    }

    if (role == StaffRole.SUPERVISOR) {
      throw new BusinessRuleException(
        "No es posible iniciar sesión como supervisor debido a que su funcionalidad no esta contemplada en la versión actual del sistema."
      );
    }
  }

  public void handleLogOut() {
    this.currentStaff = null;
    this.currentRole = null;
  }

  public StaffDTO getCurrentStaff() {
    return currentStaff;
  }

  public StaffRole getCurrentRole() {
    return currentRole;
  }
}
