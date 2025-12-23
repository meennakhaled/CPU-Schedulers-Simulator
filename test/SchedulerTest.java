import org.junit.Test;
import static org.junit.Assert.*;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SchedulerTest {

    private static final Gson gson = new Gson();

    @Test
    public void testFromJSON() {
        File folder = new File("test_cases");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files == null || files.length == 0) {
            System.out.println("No JSON test files found in test_cases folder.");
            return;
        }

        for (File jsonFile : files) {
            try {
                JsonObject json =
                        gson.fromJson(new FileReader(jsonFile), JsonObject.class);

                String testName = json.has("name")
                        ? json.get("name").getAsString()
                        : jsonFile.getName();

                System.out.println("\n--------------------------------------");
                System.out.println("Test File : " + testName);

                JsonObject input = json.getAsJsonObject("input");
                JsonArray processArray = input.getAsJsonArray("processes");

                List<Process> processes = parseProcesses(processArray);

                int contextSwitch =
                        input.has("contextSwitch")
                                ? input.get("contextSwitch").getAsInt()
                                : 0;

                int rrQuantum =
                        input.has("rrQuantum")
                                ? input.get("rrQuantum").getAsInt()
                                : 2;

                // ✅ READ AGING INTERVAL (for Priority)
                int agingInterval =
                        input.has("agingInterval")
                                ? input.get("agingInterval").getAsInt()
                                : 0;

                JsonObject expected = json.getAsJsonObject("expectedOutput");

                // ================= SJF =================
                if (expected.has("SJF")) {
                    try {
                        SchedulerResult r =
                                new SJFScheduler(processes, contextSwitch)
                                        .schedule();

                        printResult("SJF", r, expected.getAsJsonObject("SJF"));
                    } catch (AssertionError e) {
                        System.out.println("Scheduler : SJF");
                        System.out.println("❌ TEST FAILED");
                        System.out.println("Reason     : " + e.getMessage());
                    }
                }

                // ================= RR =================
                if (expected.has("RR")) {
                    try {
                        SchedulerResult r =
                                new RoundRobinScheduler(processes, contextSwitch, rrQuantum)
                                        .schedule();

                        printResult("Round Robin", r, expected.getAsJsonObject("RR"));
                    } catch (AssertionError e) {
                        System.out.println("Scheduler : Round Robin");
                        System.out.println("❌ TEST FAILED");
                        System.out.println("Reason     : " + e.getMessage());
                    }
                }

                // ================= Priority =================
                if (expected.has("Priority")) {
                    try {
                        SchedulerResult r =
                                new PriorityScheduler(processes, contextSwitch, agingInterval)
                                        .schedule();

                        printResult("Priority", r, expected.getAsJsonObject("Priority"));
                    } catch (AssertionError e) {
                        System.out.println("Scheduler : Priority");
                        System.out.println("❌ TEST FAILED");
                        System.out.println("Reason     : " + e.getMessage());
                    }
                }

                // ================= AG =================
                if (!expected.has("SJF")
                        && !expected.has("RR")
                        && !expected.has("Priority")) {
                    try {
                        AGScheduler ag = new AGScheduler();
                        SchedulerResult r =
                                ag.schedule(Process.copyList(processes), contextSwitch);

                        printResult("AG", r, expected);
                    } catch (AssertionError e) {
                        System.out.println("Scheduler : AG");
                        System.out.println("❌ TEST FAILED");
                        System.out.println("Reason     : " + e.getMessage());
                    }
                }

            } catch (Exception e) {
                System.out.println("❌ ERROR: Failed to run test");
                e.printStackTrace();
            }
        }
    }

    // ================= Helpers =================

    private List<Process> parseProcesses(JsonArray processArray) {
        List<Process> processes = new ArrayList<>();
        for (JsonElement procElement : processArray) {
            JsonObject proc = procElement.getAsJsonObject();
            processes.add(new Process(
                    proc.get("name").getAsString(),
                    proc.get("arrival").getAsInt(),
                    proc.get("burst").getAsInt(),
                    proc.get("priority").getAsInt(),
                    proc.has("quantum") ? proc.get("quantum").getAsInt() : 0
            ));
        }
        return processes;
    }
    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private void printResult(
            String name,
            SchedulerResult result,
            JsonObject expected) {

        double expectedAvgWT =
                expected.get("averageWaitingTime").getAsDouble();
        double expectedAvgTAT =
                expected.get("averageTurnaroundTime").getAsDouble();

        double actualWT;
        double actualTAT;

        if (name.equals("AG")) {
            actualWT = result.avgWaitingTime;
            actualTAT = result.avgTurnaroundTime;
        } else {
            actualWT = result.getAverageWaitingTime();
            actualTAT = result.getAverageTurnaroundTime();
        }

        // ✅ ROUND TO ONE DECIMAL
        double expWT  = roundToOneDecimal(expectedAvgWT);
        double actWT  = roundToOneDecimal(actualWT);
        double expTAT = roundToOneDecimal(expectedAvgTAT);
        double actTAT = roundToOneDecimal(actualTAT);

        System.out.println("Scheduler : " + name);
        System.out.println("Expected Avg WT : " + expWT);
        System.out.println("Actual   Avg WT : " + actWT);
        System.out.println("Expected Avg TAT: " + expTAT);
        System.out.println("Actual   Avg TAT: " + actTAT);

        boolean wtOk  = expWT == actWT;
        boolean tatOk = expTAT == actTAT;

        if (wtOk && tatOk) {
            System.out.println("✅ TEST PASSED");
        } else {
            System.out.println("❌ TEST FAILED");
            System.out.println("Reason     : Mismatch in results");
        }

        System.out.println("--------------------------------------");
    }

}
