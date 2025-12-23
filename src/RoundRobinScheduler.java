import java.util.*;

class RoundRobinScheduler extends Scheduler {
    private int timeQuantum;

    public RoundRobinScheduler(List<Process> processes, int contextSwitchTime, int timeQuantum) {
        super(processes, contextSwitchTime);
        this.timeQuantum = timeQuantum;
    }

    @Override
    public SchedulerResult schedule() {
        int currentTime = 0;
        Queue<Process> readyQueue = new LinkedList<>();
        Set<String> inQueue = new HashSet<>();

        // Sort processes by arrival time
        List<Process> sortedProcesses = new ArrayList<>(processes);
        sortedProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));

        int processIndex = 0;

        while (!allProcessesCompleted() || !readyQueue.isEmpty()) {
            // Add newly arrived processes to ready queue
            while (processIndex < sortedProcesses.size() &&
                    sortedProcesses.get(processIndex).getArrivalTime() <= currentTime) {
                Process p = sortedProcesses.get(processIndex);
                if (!p.isCompleted() && !inQueue.contains(p.getName())) {
                    readyQueue.offer(p);
                    inQueue.add(p.getName());
                }
                processIndex++;
            }

            if (readyQueue.isEmpty()) {
                // No process in ready queue, jump to next arrival
                if (processIndex < sortedProcesses.size()) {
                    currentTime = sortedProcesses.get(processIndex).getArrivalTime();
                }
                continue;
            }

            // Get next process from ready queue
            Process currentProcess = readyQueue.poll();
            inQueue.remove(currentProcess.getName());

            // Apply context switch
            currentTime = performContextSwitch(currentTime, currentProcess.getName());

            // Set start time
            currentProcess.setStartTime(currentTime);

            // Calculate execution time
            int executionTime = Math.min(timeQuantum, currentProcess.getRemainingTime());

            // Execute process
            addToExecutionOrder(currentProcess.getName());
            currentProcess.execute(executionTime);
            currentTime += executionTime;

            // Add newly arrived processes during execution
            while (processIndex < sortedProcesses.size() &&
                    sortedProcesses.get(processIndex).getArrivalTime() <= currentTime) {
                Process p = sortedProcesses.get(processIndex);
                if (!p.isCompleted() && !inQueue.contains(p.getName()) &&
                        !p.equals(currentProcess)) {
                    readyQueue.offer(p);
                    inQueue.add(p.getName());
                }
                processIndex++;
            }

            // Handle process completion or re-queue
            if (currentProcess.isCompleted()) {
                currentProcess.setCompletionTime(currentTime);
            } else {
                readyQueue.offer(currentProcess);
                inQueue.add(currentProcess.getName());
            }
        }

        finalizeResults();
        return result;
    }
}
