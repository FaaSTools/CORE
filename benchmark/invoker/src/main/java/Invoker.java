import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jFaaS.Gateway;
import jFaaS.utils.PairResult;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(name = "java -jar invoker-1.0.jar", mixinStandardHelpOptions = true)
public class Invoker implements Callable<Integer> {

  @CommandLine.Option(
      names = {"-c", "--credentials"},
      description = "Path to credentials file")
  private String credentials = "credentials.properties";

  @CommandLine.Option(
      names = {"-r", "--runs"},
      description = "Number of experiments")
  @SuppressWarnings("final")
  private int runs = 11;

  @CommandLine.Option(
      names = {"-i", "--input"},
      description = "The path to input JSON config file",
      required = true)
  private String inputPath;

  @CommandLine.Option(
      names = {"-o", "--output"},
      description = "The path to the output file",
      required = true)
  private String outputPath;

  @CommandLine.Option(
      names = {"-s", "--sleep"},
      description = "If specified, sleep x amount of ms between requests")
  private int sleep;

  public static void main(String[] args) throws IOException {
    int exitCode = new CommandLine(new Invoker()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      Path input = Path.of(inputPath);
      Path output = Path.of(outputPath);
      Map<String, Object> functionInput;

      try (InputStream in = Files.newInputStream(input, StandardOpenOption.READ)) {
        functionInput = objectMapper.readValue(in, new TypeReference<>() {});
      }
      final String function = (String) functionInput.get("function");

      Gateway gateway = new Gateway(credentials);
      List<Map<String, Object>> results = new ArrayList<>();
      for (int i = 0; i < runs; i++) {
        // invoke function
        PairResult<String, Long> pairResult = gateway.invokeFunction(function, functionInput);
        Map<String, Object> functionResult =
            objectMapper.readValue(pairResult.getResult(), new TypeReference<>() {});
        functionResult.put("roundTripTime", pairResult.getRTT());
        // remove cold start measurement
        if (i > 0) {
          results.add(functionResult);
        }
        System.out.printf("Round trip time run %d: %d ms\n", i, pairResult.getRTT());
        if (sleep > 0) {
          Thread.sleep(sleep);
        }
      }
      Files.createDirectories(output.getParent());
      try (var writer =
          Files.newBufferedWriter(
              output,
              StandardOpenOption.CREATE,
              StandardOpenOption.TRUNCATE_EXISTING,
              StandardOpenOption.WRITE)) {
        String resultJson = new Gson().toJson(results);
        writer.write(resultJson);
      }
      return 0;
    } catch (final Exception e) {
      System.out.println(e.getMessage());
      return 1;
    }
  }
}
