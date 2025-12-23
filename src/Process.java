import java.util.*;

class Process {
    private String name;
    private int arrivalTime;
    private int burstTime;
    private int remainingTime;
    private int priority;
    private int quantum;
    private int waitingTime;
    private int turnaroundTime;
    private int completionTime;
    private int startTime;  // First time process started execution
    private boolean hasStarted;

    // ===== ADDED to match model.Process =====
    private int initialQuantum;
    private int totalBurstTime;
    private int dynamicPriority;
    // =======================================

    // For AG Scheduling - track quantum history
    private List<Integer> quantumHistory;

    // For AG Scheduling - track execution phases
    private double fcfsPhase;      // 25% of quantum
    private double priorityPhase;  // next 25% of quantum (50% total)
    private double sjfPhase;       // remaining 50%
    private int executedInCurrentQuantum; // Track how much executed in current quantum

    public Process(String name, int arrivalTime, int burstTime, int priority, int quantum) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.quantum = quantum;

        // ===== ADDED =====
        this.initialQuantum = quantum;
        this.totalBurstTime = burstTime;
        this.dynamicPriority = priority;
        // =================

        this.waitingTime = 0;
        this.turnaroundTime = 0;
        this.completionTime = 0;
        this.startTime = -1;
        this.hasStarted = false;
        this.quantumHistory = new ArrayList<>();
        this.quantumHistory.add(quantum);
        this.executedInCurrentQuantum = 0;
        calculateAGPhases();
    }

    // Calculate the phase boundaries for AG scheduling
    private void calculateAGPhases() {
        this.fcfsPhase = Math.ceil(quantum * 0.25);
        this.priorityPhase = Math.ceil(quantum * 0.25);
        this.sjfPhase = quantum - fcfsPhase - priorityPhase;
    }

    public void updateQuantum(int newQuantum) {
        this.quantum = newQuantum;
        this.quantumHistory.add(newQuantum);
        this.executedInCurrentQuantum = 0;
        calculateAGPhases();
    }

    // ===== ADDED for AG Scheduler =====
    public void setCurrentQuantum(int q) {
        this.quantum = q;
        this.quantumHistory.add(q);
        this.executedInCurrentQuantum = 0;
        calculateAGPhases();
    }
    // ==================================

    public void execute(int time) {
        if (!hasStarted) {
            hasStarted = true;
        }
        this.remainingTime -= time;
        this.executedInCurrentQuantum += time;
        if (this.remainingTime < 0) {
            this.remainingTime = 0;
        }
    }

    public boolean isCompleted() {
        return remainingTime == 0;
    }

    public void setCompletionTime(int time) {
        this.completionTime = time;
        this.turnaroundTime = completionTime - arrivalTime;
        this.waitingTime = turnaroundTime - burstTime;
    }

    public void setStartTime(int time) {
        if (this.startTime == -1) {
            this.startTime = time;
        }
    }

    // ===== ADDED for AG Scheduler =====
    public int getInitialQuantum() {
        return initialQuantum;
    }

    public int getCurrentQuantum() {
        return quantum;
    }

    public int getRemainingBurstTime() {
        return remainingTime;
    }

    public int getDynamicPriority() {
        return dynamicPriority;
    }

    public int getTotalBurstTime() {
        return totalBurstTime;
    }

    public void logQuantumUpdate() {
        // optional – for debugging or printing
    }

    public void setDynamicPriority(int p) {
        this.dynamicPriority = p;
    }
    // ==================================

    // Getters
    public String getName() { return name; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getRemainingTime() { return remainingTime; }
    public int getPriority() { return priority; }
    public int getQuantum() { return quantum; }
    public int getWaitingTime() { return waitingTime; }
    public int getTurnaroundTime() { return turnaroundTime; }
    public int getCompletionTime() { return completionTime; }
    public int getStartTime() { return startTime; }
    public boolean hasStarted() { return hasStarted; }
    public List<Integer> getQuantumHistory() { return new ArrayList<>(quantumHistory); }
    public double getFcfsPhase() { return fcfsPhase; }
    public double getPriorityPhase() { return priorityPhase; }
    public double getSjfPhase() { return sjfPhase; }
    public int getExecutedInCurrentQuantum() { return executedInCurrentQuantum; }

    // Setters
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }
    public void setPriority(int priority) {
        this.priority = priority;
        this.dynamicPriority = priority;
    }
    public void resetExecutedInCurrentQuantum() { this.executedInCurrentQuantum = 0; }

    @Override
    public String toString() {
        return String.format(
                "Process[%s, Arrival=%d, Burst=%d, Remaining=%d, Priority=%d, Quantum=%d]",
                name, arrivalTime, burstTime, remainingTime, priority, quantum
        );
    }

    // ===== ADDED copyList to match model.Process =====
    public static ArrayList<Process> copyList(List<Process> input) {
        ArrayList<Process> copy = new ArrayList<>();
        for (Process p : input) {
            Process np = new Process(
                    p.name,
                    p.arrivalTime,
                    p.burstTime,
                    p.priority,
                    p.initialQuantum
            );
            np.dynamicPriority = p.dynamicPriority;
            copy.add(np);
        }
        return copy;
    }
    // ===============================================
    public Process copy() {
        Process p = new Process(name, arrivalTime, burstTime, priority, quantumHistory.get(0));
        return p;
    }

}