package in.dailyhunt.personalization;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import org.cfg4j.provider.ConfigurationProvider;

public class Test {

  @Inject
  private static LrConfig config;

  public static void main(String[] args) {

    System.out.println(config.lrEnabled());
    System.out.println(config.modelPath());

  }
}