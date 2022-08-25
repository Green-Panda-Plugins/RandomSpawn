package dev.michaud.greenpanda.randomspawnpoint.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import org.jetbrains.annotations.Nullable;

public class ServerProperties {

  @Nullable
  public static String getString(String key) {

    try {

      BufferedReader reader = new BufferedReader(new FileReader("server.properties"));
      Properties properties = new Properties();

      properties.load(reader);
      reader.close();

      return properties.getProperty(key);

    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

  }

}