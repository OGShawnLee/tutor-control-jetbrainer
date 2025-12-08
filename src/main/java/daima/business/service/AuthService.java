package daima.business.service;

import daima.business.dao.StaffDAO;
import daima.business.dto.StaffDTO;
import daima.common.BusinessRuleException;
import daima.common.UserDisplayableException;

import java.util.Optional;

public class AuthService {
  private static final AuthService INSTANCE = new AuthService();
  private static final StaffDAO STAFF_DAO = StaffDAO.getInstance();

  public static AuthService getInstance() {
    return INSTANCE;
  }

  public static class SignInResult {
    private final StaffDTO staffDTO;
    private final boolean roleSelectionRequired;

    public SignInResult(StaffDTO staffDTO, boolean roleSelectionRequired) {
      this.staffDTO = staffDTO;
      this.roleSelectionRequired = roleSelectionRequired;
    }

    public StaffDTO getStaffDTO() {
      return staffDTO;
    }

    public boolean isRoleSelectionRequired() {
      return roleSelectionRequired;
    }
  }

  public SignInResult handleSignIn(String email, String password) throws UserDisplayableException {
    Optional<StaffDTO> staffDTOOptional = STAFF_DAO.findOneByEmail(email);

    if (staffDTOOptional.isPresent()) {
      StaffDTO staffDTO = staffDTOOptional.get();
      if (staffDTO.hasPasswordMatch(password)) {
        return new SignInResult(staffDTO, staffDTO.getRoles().size() > 1);
      }
    }

    throw new BusinessRuleException("No ha sido posible iniciar sesión. Las credenciales son invalidas.");
  }
}
