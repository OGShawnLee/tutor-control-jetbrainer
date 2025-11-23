package daima.business.dto;

import java.time.LocalDateTime;

public class ProgramDTO implements Record {
  private int id;
  private final String name;
  private final String acronym;
  private LocalDateTime createdAt;

  public ProgramDTO(String name, String acronym) {
    this.name = name;
    this.acronym = acronym;
    this.createdAt = LocalDateTime.of(2020, 6, 1, 12, 0);
  }

  public int getID() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAcronym() {
    return acronym;
  }

  @Override
  public String toString() {
    return acronym + " (" + name + ")";
  }

  @Override
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
