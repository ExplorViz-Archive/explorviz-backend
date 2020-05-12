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
      System.out.println("Do startup logic here");
      KiekerAdapter adapter = KiekerAdapter.getInstance();
      adapter.startReader();
      return 0;
    }
  }
}
