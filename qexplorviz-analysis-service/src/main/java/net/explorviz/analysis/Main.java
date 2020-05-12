package net.explorviz.analysis;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import net.explorviz.kiekeradapter.main.KiekerAdapter;

@QuarkusMain
public class Main {
  public static void main(String... args) {
    Quarkus.run(MyApp.class, args);
  }

  public static class MyApp implements QuarkusApplication {

    @Override
    public int run(String... args) throws Exception {
      KiekerAdapter adapter = KiekerAdapter.getInstance();
      adapter.startReader();
      Quarkus.waitForExit();
      return 0;
    }
  }
}
