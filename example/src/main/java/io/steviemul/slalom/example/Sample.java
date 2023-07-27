package io.steviemul.slalom.example;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SampleAnnotation
public final class Sample extends SampleParent implements SampleInterface1, SampleInterface2 {

  static {
    int i = 10;
  }

  private static String DB_USERNAME = "username";
  private static final String DB_PASSWORD = "password";
  private java.security.MessageDigest md = null;

  static {
    String username = "username";
    DB_USERNAME = username;
  }

  private String name;

  private int x = 5, y = 10;

  private List<String> addressLines = new ArrayList<>();
  private InnerSample innerSample = new InnerSample();

  public String getName() {
    return this.name;
  }

  public Sample(String name) throws Exception {
    this.name = name;
    md = java.security.MessageDigest.getInstance("MD5");
  }

  public List<String> getLines() {
    return addressLines.stream().filter(e -> e.contains("test")).collect(Collectors.toList());
  }

  private ResultSet execute(String query) throws Exception {
    Connection con =
        DriverManager.getConnection("jdbc:mysql://db.com:3306/core", DB_USERNAME, DB_PASSWORD);

    Statement stmt = con.createStatement();

    return stmt.executeQuery(query);
  }

  public String getDetails() {
    try {
      String query = "SELECT age FROM USERS WHERE name='" + name + "'";

      ResultSet result = execute(query);

      return result.getString(0);
    } catch (Exception e) {
      return null;
    }
  }

  @SampleAnnotation(uri = "/app/users/{name}")
  public void executeRawQuery(String name) throws Exception {
    String query = "SELECT age FROM USERS WHERE name='" + name + "'";

    execute(query);
  }

  public static void main(String[] args) throws Exception {
    Sample sample = new Sample(args[0]);

    String details = sample.getDetails();

    if (details.length() > 10) {
      System.out.println("Details are big");
    } else {
      System.out.println("Details are normal");
    }

    System.out.println(details);
  }

  public static void output(String[] args) throws Exception {
    int index = 0;

    java.io.OutputStream out = System.out;

    Sample sample = new Sample(args[index]);

    out.write(sample.toString().getBytes(StandardCharsets.UTF_8));
  }

  static class InnerSample {
    private String name;

    String getName() {
      return this.name;
    }

    void setName(String name) {
      this.name = name;
    }
  }
}
