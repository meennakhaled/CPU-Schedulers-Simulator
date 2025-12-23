
import java.util.*;

class SchedulerResult {
    public List<String> executionOrder = new ArrayList<>();
    private Map<String, Integer> waitingTimes;
    private Map<String, Integer> turnaroundTimes;
    private double averageWaitingTime;
    private double averageTurnaroundTime;
    private Map<String, List<Integer>> quantumHistory; // For AG scheduling


    public List<Process> processes = new ArrayList<>();
    public double avgWaitingTime;
    public double avgTurnaroundTime;



    public SchedulerResult() {
        this.executionOrder = new ArrayList<>();
        this.waitingTimes = new HashMap<>();
        this.turnaroundTimes = new HashMap<>();
        this.quantumHistory = new HashMap<>();
    }

    public void addToExecutionOrder(String processName) {
        executionOrder.add(processName);
    }

    public void setProcessTimes(String processName, int waitingTime, int turnaroundTime) {
        waitingTimes.put(processName, waitingTime);
        turnaroundTimes.put(processName, turnaroundTime);
    }

    public void setQuantumHistory(String processName, List<Integer> history) {
        quantumHistory.put(processName, history);
    }

    public void calculateAverages() {
        if (!waitingTimes.isEmpty()) {
            averageWaitingTime = waitingTimes.values().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
            averageTurnaroundTime = turnaroundTimes.values().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
        }
    }

    // Getters
    public List<String> getExecutionOrder() {
        return executionOrder;
    }

    public Map<String, Integer> getWaitingTimes() {
        return waitingTimes;
    }

    public Map<String, Integer> getTurnaroundTimes() {
        return turnaroundTimes;
    }

    public double getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public double getAverageTurnaroundTime() {
        return averageTurnaroundTime;
    }

    public Map<String, List<Integer>> getQuantumHistory() {
        return quantumHistory;
    }

    public void printResults(String schedulerName) {
        String line = repeatChar('=', 60);
        System.out.println("\n" + line);
        System.out.println(schedulerName + " Scheduling Results");
        System.out.println(line);

        // Print execution order
        System.out.println("\nExecution Order:");
        System.out.print("  ");
        for (int i = 0; i < executionOrder.size(); i++) {
            System.out.print(executionOrder.get(i));
            if (i < executionOrder.size() - 1) {
                System.out.print(" -> ");
            }
        }
        System.out.println();

        // Print waiting times
        System.out.println("\nWaiting Times:");
        for (Map.Entry<String, Integer> entry : waitingTimes.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " ms");
        }

        // Print turnaround times
        System.out.println("\nTurnaround Times:");
        for (Map.Entry<String, Integer> entry : turnaroundTimes.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " ms");
        }

        // Print averages
        System.out.println("\nAverage Waiting Time: " + String.format("%.1f", averageWaitingTime) + " ms");
        System.out.println("Average Turnaround Time: " + String.format("%.1f", averageTurnaroundTime) + " ms");

        // Print quantum history for AG scheduling
        if (!quantumHistory.isEmpty()) {
            System.out.println("\nQuantum History (AG Scheduling):");
            for (Map.Entry<String, List<Integer>> entry : quantumHistory.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }

        System.out.println(line + "\n");
    }

    // Helper method for Java 8 compatibility
    private String repeatChar(char ch, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }
}