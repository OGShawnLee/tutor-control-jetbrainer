package daima.business.dao;

import daima.business.dto.StaffDTO;
import daima.business.enumeration.StaffRole;

import java.util.ArrayList;
import java.util.Arrays;

public class StaffDAO {
  private final static StaffDAO INSTANCE = new StaffDAO();

  public static StaffDAO getInstance() {
    return INSTANCE;
  }

  public ArrayList<StaffDTO> getAll() {
    return new ArrayList<>(Arrays.asList(
      new StaffDTO("Damian", "Lee", "Damian@lee.com", "001", StaffRole.ADMINISTRATOR),
      new StaffDTO("Jackie", "Lee", "Jackie@lee.com", "002", StaffRole.COORDINATOR)
    ));
  }

  public ArrayList<StaffDTO> getAllByProgramAndRole(int idProgram, StaffRole role) {
    return new ArrayList<>(Arrays.asList(
      new StaffDTO("Damian", "Lee", "Damian@lee.com", "001", role),
      new StaffDTO("Jackie", "Lee", "Jackie@lee.com", "002", role)
    ));
  }
}
