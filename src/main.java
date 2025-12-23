import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class main {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("        OS SCHEDULER CONFIGURATION       ");
        System.out.println("========================================");

        System.out.print("Enter number of processes: ");
        int n = in.nextInt();

        System.out.print("Enter context switching time: ");
        int contextSwitch = in.nextInt();

        System.out.print("Enter Round Robin Quantum: ");
        int rrQuantum = in.nextInt();

        System.out.print("Enter Aging Interval (Priority): ");
        int agingInterval = in.nextInt();

        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Process " + (i + 1) + " ---");
            System.out.print("Name: ");
            String name = in.next();
            System.out.print("Arrival Time: ");
            int arrival = in.nextInt();
            System.out.print("Burst Time: ");
            int burst = in.nextInt();
            System.out.print("Priority: ");
            int priority = in.nextInt();
            System.out.print("AG Quantum: ");
            int quantum = in.nextInt();

            processes.add(new Process(name, arrival, burst, priority, quantum));
        }

        // ================= SJF =================
        runScheduler(
                "Shortest Job First",
                new SJFScheduler(Process.copyList(processes), contextSwitch).schedule()
        );

        // ================= RR =================
        runScheduler(
                "Round Robin",
                new RoundRobinScheduler(Process.copyList(processes), contextSwitch, rrQuantum).schedule()
        );

        // ================= Priority =================
        runScheduler(
                "Priority (With Aging)",
                new PriorityScheduler(Process.copyList(processes), contextSwitch, agingInterval).schedule()
        );

        // ================= AG =================
        System.out.println("\n========================================");
        System.out.println("RUNNING: AG SCHEDULER");
        System.out.println("========================================");

        AGScheduler ag = new AGScheduler();
        SchedulerResult agResult =
                ag.schedule(Process.copyList(processes), contextSwitch);

        printExecutionOrder(agResult);
        printTable(agResult.processes);

        System.out.printf("\nAverage Waiting Time: %.2f\n", agResult.avgWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f\n", agResult.avgTurnaroundTime);

        System.out.println("========================================");

        in.close();
    }

    // ================= Helpers =================

    private static void runScheduler(String name, SchedulerResult result) {
        System.out.println("\n========================================");
        System.out.println("RUNNING: " + name);
        System.out.println("========================================");

        printExecutionOrder(result);
        printTable(result.processes);

        System.out.printf("\nAverage Waiting Time: %.2f\n",
                result.getAverageWaitingTime());
        System.out.printf("Average Turnaround Time: %.2f\n",
                result.getAverageTurnaroundTime());

        System.out.println("========================================");
    }

    private static void printExecutionOrder(SchedulerResult result) {
        if (result.executionOrder != null && !result.executionOrder.isEmpty()) {
            System.out.println("Execution Order:");
            System.out.println(String.join(" -> ", result.executionOrder));
        }
    }

    private static void printTable(List<Process> processes) {
        System.out.println("\nMETRIC TABLE:");
        System.out.println("---------------------------------------------------------");
        System.out.printf("%-10s | %-10s | %-10s | %-10s\n",
                "Process", "Burst", "Waiting", "Turnaround");
        System.out.println("---------------------------------------------------------");

        for (Process p : processes) {
            System.out.printf("%-10s | %-10d | %-10d | %-10d\n",
                    p.getName(),
                    p.getTotalBurstTime(),
                    p.getWaitingTime(),
                    p.getTurnaroundTime());
        }

        System.out.println("---------------------------------------------------------");
    }
}
