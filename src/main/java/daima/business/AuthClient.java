package daima.business;

import daima.business.dto.StaffDTO;

/*
 * AuthClient is a singleton class that manages the authentication state of the application.
 * It holds information about the currently logged-in user and provides methods to access and modify this information.
 */
public class AuthClient {
  private StaffDTO currentStaff;
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

  public StaffDTO getCurrentStaff() {
    return currentStaff;
  }

  public void setCurrentStaff(StaffDTO currentStaff) {
    this.currentStaff = currentStaff;
  }
}
