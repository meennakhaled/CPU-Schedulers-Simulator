import java.util.*;

abstract class Scheduler {
    protected List<Process> processes;
    protected int contextSwitchTime;
    protected SchedulerResult result;
    protected String lastExecutedProcess; // To track context switches

    public Scheduler(List<Process> processes, int contextSwitchTime) {
        // Create copies of processes to avoid modifying originals
        this.processes = new ArrayList<>();
        for (Process p : processes) {
            this.processes.add(p.copy());
        }
        this.contextSwitchTime = contextSwitchTime;
        this.result = new SchedulerResult();
        this.lastExecutedProcess = null;
    }

    // Main method that each scheduler must implement
    public abstract SchedulerResult schedule();

    // Helper method to add to execution order
    protected void addToExecutionOrder(String processName) {
        result.addToExecutionOrder(processName);
    }

    // Helper method to check if context switch is needed
    protected boolean needsContextSwitch(String processName) {
        return lastExecutedProcess != null && !lastExecutedProcess.equals(processName);
    }

    // Helper method to perform context switch
    protected int performContextSwitch(int currentTime, String newProcessName) {
        if (needsContextSwitch(newProcessName)) {
            currentTime += contextSwitchTime;
        }
        lastExecutedProcess = newProcessName;
        return currentTime;
    }

    // Helper method to get processes that have arrived by a given time
    protected List<Process> getArrivedProcesses(int currentTime) {
        List<Process> arrived = new ArrayList<>();
        for (Process p : processes) {
            if (p.getArrivalTime() <= currentTime && !p.isCompleted()) {
                arrived.add(p);
            }
        }
        return arrived;
    }

    // Helper method to check if all processes are completed
    protected boolean allProcessesCompleted() {
        for (Process p : processes) {
            if (!p.isCompleted()) {
                return false;
            }
        }
        return true;
    }

    // Helper method to find the next arrival time
    protected int getNextArrivalTime(int currentTime) {
        int nextArrival = Integer.MAX_VALUE;
        for (Process p : processes) {
            if (p.getArrivalTime() > currentTime && !p.isCompleted()) {
                nextArrival = Math.min(nextArrival, p.getArrivalTime());
            }
        }
        return nextArrival;
    }

    // Helper method to find process by name
    protected Process getProcessByName(String name) {
        for (Process p : processes) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    // Finalize results after scheduling is complete
    protected void finalizeResults() {
        for (Process p : processes) {
            result.setProcessTimes(p.getName(), p.getWaitingTime(), p.getTurnaroundTime());
            result.setQuantumHistory(p.getName(), p.getQuantumHistory());
        }
        result.calculateAverages();
    }
}
