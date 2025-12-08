package daima.business.dto;

import daima.business.validator.Validator;
import daima.common.InvalidFieldException;

import java.time.LocalDateTime;

public abstract class Person implements Record, Searchable {
  private int id;
  private String name;
  private String lastName;
  private String email;
  private LocalDateTime createdAt;

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) throws InvalidFieldException {
    this.name = Validator.getValidFlexibleName(name, "Nombre", 2, 64);
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) throws InvalidFieldException {
    this.lastName = Validator.getValidFlexibleName(lastName, "Apellido", 2, 64);
  }

  public String getFullName() {
    return name + " " + lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) throws InvalidFieldException {
    this.email = Validator.getValidEmail(email);
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public String getSearchableText() {
    return String.format("%s %s %s",
      getFullName(),
      getEmail(),
      getFormattedCreatedAt()
    ).toLowerCase();
  }
}
