
import java.util.*;

public class SchedulerSimulator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String line = repeatString("=", 60);
        System.out.println(line);
        System.out.println("CPU Scheduling Simulator");
        System.out.println(line);

        // Choose input method
        System.out.println("\n1. Use test case data");
        System.out.println("2. Enter custom data");
        System.out.print("Choose option: ");
        int option = scanner.nextInt();

        List<Process> processes;
        int contextSwitchTime;
        int rrQuantum;

        if (option == 1) {
            processes = getTestCase1();
            contextSwitchTime = 1;
            rrQuantum = 2;
        } else {
            // Input: Number of processes
            System.out.print("\nEnter number of processes: ");
            int numProcesses = scanner.nextInt();

            // Input: Context switching time
            System.out.print("Enter context switching time: ");
            contextSwitchTime = scanner.nextInt();

            // Input: Round Robin time quantum
            System.out.print("Enter Round Robin time quantum: ");
            rrQuantum = scanner.nextInt();

            processes = new ArrayList<>();

            // Input process details
            for (int i = 0; i < numProcesses; i++) {
                System.out.println("\nProcess " + (i + 1) + ":");
                System.out.print("  Name: ");
                String name = scanner.next();
                System.out.print("  Arrival Time: ");
                int arrivalTime = scanner.nextInt();
                System.out.print("  Burst Time: ");
                int burstTime = scanner.nextInt();
                System.out.print("  Priority: ");
                int priority = scanner.nextInt();
                System.out.print("  Quantum (for AG): ");
                int quantum = scanner.nextInt();

                processes.add(new Process(name, arrivalTime, burstTime, priority, quantum));
            }
        }

        // Run schedulers
        System.out.println("\n" + line);
        System.out.println("Starting Scheduling Simulations...");
        System.out.println(line);

        // 1. SJF Preemptive (EXAMPLE - Already implemented!)
        Scheduler sjfScheduler = new SJFScheduler(processes, contextSwitchTime);
        SchedulerResult sjfResult = sjfScheduler.schedule();
        sjfResult.printResults("Preemptive SJF");

        scanner.close();
    }

    // Test case 1 from assignment
    public static List<Process> getTestCase1() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", 0, 3, 3, 0));
        processes.add(new Process("P2", 1, 2, 1, 0));
        processes.add(new Process("P3", 2, 4, 2, 0));
        processes.add(new Process("P4", 3, 1, 4, 0));
        processes.add(new Process("P5", 4, 3, 5, 0));
        return processes;
    }

    // Test case for AG scheduling
    public static List<Process> getTestCaseAG() {
        List<Process> processes = new ArrayList<>();
        processes.add(new Process("P1", 0, 3, 3, 0));
        processes.add(new Process("P2", 1, 2, 1, 0));
        processes.add(new Process("P3", 2, 4, 2, 0));
        processes.add(new Process("P4", 4, 3, 5, 0));

        return processes;
    }

    // Helper method for Java 8 compatibility
    private static String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}