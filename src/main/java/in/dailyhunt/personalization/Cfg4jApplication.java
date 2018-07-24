package in.dailyhunt.personalization;

import com.codahale.metrics.MetricRegistry;
import com.google.gson.Gson;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import org.apache.http.client.utils.DateUtils;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.provider.GenericType;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.git.GitConfigurationSourceBuilder;
import org.cfg4j.source.reload.Reloadable;
import org.cfg4j.source.reload.strategy.PeriodicalReloadStrategy;


public class Cfg4jApplication {

  private ConfigurationSource configSource;
  private PeriodicalReloadStrategy reloadStrategy;

  private String gitRepo;
  private String gitBranch;
  private int reloadPeriodInSeconds;

  public  Cfg4jApplication() {}

  public Cfg4jApplication(String gitRepo, String gitBranch, int reloadPeriodInSeconds) {
    this.gitRepo = gitRepo;
    this.gitBranch = gitBranch;
    this.reloadPeriodInSeconds = reloadPeriodInSeconds;
  }

  private void init() throws InterruptedException {

    this.configSource = new GitConfigurationSourceBuilder()
        .withRepositoryURI("https://git.newshunt.com/saumil.patel/lr-config.git")
        .withConfigFilesProvider(() -> Arrays.asList(Paths.get("lr-config.yaml")))
        .build();

    Environment environment = new ImmutableEnvironment("master");
    this.reloadStrategy = new PeriodicalReloadStrategy(5, TimeUnit.SECONDS);

    ConfigurationProvider configProvider = new ConfigurationProviderBuilder()
        .withConfigurationSource(configSource)
        .withEnvironment(environment)
        .withReloadStrategy(reloadStrategy)
        .withMetrics(new MetricRegistry(), "lr-config-timer")
        .build();

    boolean cc1 = configProvider.getProperty("lrEnabled", Boolean.class);

    while (true) {
      NestedConfig property = configProvider.getProperty("nestedConfig", NestedConfig.class);
      System.out.println(new Date() + " " + new Gson().toJson(property));
      Thread.sleep(5000);
    }

  }

  public void close() {
    this.reloadStrategy.deregister((Reloadable) configSource);
    try {
      Field timerField = reloadStrategy.getClass().getDeclaredField("timer");
      timerField.setAccessible(true);
      Timer timer = (Timer) timerField.get(reloadStrategy);
      timer.cancel();
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Cfg4jApplication app = new Cfg4jApplication();
    app.init();
    System.out.println("DONE");
    app.close();
  }
}
