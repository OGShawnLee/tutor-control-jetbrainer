package daima.business.dao;

import daima.business.dto.ProgramDTO;

import java.util.ArrayList;
import java.util.Arrays;

public class ProgramDAO {
  private final static ProgramDAO INSTANCE = new ProgramDAO();

  public static ProgramDAO getInstance() {
    return INSTANCE;
  }

  public ArrayList<ProgramDTO> getAll() {
    return new ArrayList<>(Arrays.asList(
      new ProgramDTO("Ingeniería en Sistemas Computacionales", "ISC"),
      new ProgramDTO("Ingeniería Industrial", "II"),
      new ProgramDTO("Ingeniería Mecatrónica", "IMT"),
      new ProgramDTO("Licenciatura en Administración", "LA"),
      new ProgramDTO("Licenciatura en Contaduría", "LC")
    ));
  }
}
