package daima.business.dto;

import java.time.LocalDateTime;
import java.util.Optional;

public class ProgramDTO implements Record, Searchable {
  private int id;
  private int idCoordinator;
  private String nameCoordinator;
  private String name;
  private String acronym;
  private LocalDateTime createdAt;

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
  }

  public Optional<Integer> getIDCoordinator() {
    return Optional.ofNullable(idCoordinator == 0 ? null : idCoordinator);
  }

  public void setIDCoordinator(int idCoordinator) {
    this.idCoordinator = idCoordinator;
  }

  public Optional<String> getNameCoordinator() {
    return Optional.ofNullable(nameCoordinator);
  }

  public void setNameCoordinator(String nameCoordinator) {
    this.nameCoordinator = nameCoordinator;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAcronym() {
    return acronym;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  @Override
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", acronym, name);
  }

  @Override
  public String getSearchableText() {
    return String.format("%s %s %s %s",
      getAcronym(),
      getName(),
      getNameCoordinator(),
      getFormattedCreatedAt()
    ).toLowerCase();
  }
}
