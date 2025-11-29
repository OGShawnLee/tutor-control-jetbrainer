package daima.business.dao;

import daima.business.dto.TutoredDTO;

import java.util.ArrayList;
import java.util.Arrays;

public class TutoredDAO {
  private final static TutoredDAO INSTANCE = new TutoredDAO();

  public static TutoredDAO getInstance() {
    return INSTANCE;
  }

  public ArrayList<TutoredDTO> getAll() {
    return new ArrayList<>(Arrays.asList(
      new TutoredDTO("Damian", "Lee", "Damian@lee.com", "23014115", 0),
      new TutoredDTO("Jackie", "Lee", "Jackie@lee.com", "23014113", 0),
      new TutoredDTO("Light", "Lee", "Jackie@lee.com", "23014111", 0)
    ));
  }

  public ArrayList<TutoredDTO> getAllByTutor(int idTutor) {
    return getAll();
  }
}
