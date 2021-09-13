package modules;

import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.sunbird.auth.verifier.KeyManager;
import org.sunbird.http.HttpClientUtil;
import org.sunbird.keys.JsonKey;
import org.sunbird.util.ProjectUtil;
import org.sunbird.util.Util;
import org.sunbird.util.user.SchedulerManager;
import play.api.Environment;
import play.api.inject.ApplicationLifecycle;

@Singleton
public class ApplicationStart {
  public static ProjectUtil.Environment env;
  public static String ssoPublicKey = "";

  @Inject
  public ApplicationStart(ApplicationLifecycle applicationLifecycle, Environment environment) {
    setEnvironment(environment);
    ssoPublicKey = System.getenv(JsonKey.SSO_PUBLIC_KEY);
    checkCassandraConnections();
    HttpClientUtil.getInstance();
    applicationLifecycle.addStopHook(() -> CompletableFuture.completedFuture(null));
    KeyManager.init();
  }

  private void setEnvironment(Environment environment) {
    if (environment.asJava().isDev()) {
      env = ProjectUtil.Environment.dev;
    } else if (environment.asJava().isTest()) {
      env = ProjectUtil.Environment.qa;
    } else {
      env = ProjectUtil.Environment.prod;
    }
  }

  private static void checkCassandraConnections() {
    Util.checkCassandraDbConnections();
    SchedulerManager.schedule();
  }
}
