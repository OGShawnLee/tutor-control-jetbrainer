package daima.business.dto;

import java.time.LocalDateTime;

public abstract class Person implements Record {
  private int id;
  private final String name;
  private final String lastName;
  private final String email;
  private LocalDateTime createdAt;

  public Person(String name, String lastName, String email) {
    this.id = 0;
    this.name = name;
    this.lastName = lastName;
    this.email = email;
    this.createdAt = LocalDateTime.of(2020, 6, 1, 12, 0);
  }

  public int getID() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
