import java.util.*;

class SJFScheduler extends Scheduler {

    public SJFScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
    }

    @Override
    public SchedulerResult schedule() {
        int currentTime = 0;

        while (!allProcessesCompleted()) {
            List<Process> arrivedProcesses = getArrivedProcesses(currentTime);

            if (arrivedProcesses.isEmpty()) {
                // No process available, jump to next arrival
                currentTime = getNextArrivalTime(currentTime);
                continue;
            }

            // Select process with shortest remaining time
            Process selectedProcess = arrivedProcesses.stream()
                    .min(Comparator.comparingInt(Process::getRemainingTime)
                            .thenComparingInt(Process::getArrivalTime))
                    .orElse(null);

            if (selectedProcess == null)
                break;

            // Apply context switch if needed
            currentTime = performContextSwitch(currentTime, selectedProcess.getName());

            // Add to execution order
            addToExecutionOrder(selectedProcess.getName());

            // Set start time if this is the first execution
            selectedProcess.setStartTime(currentTime);

            // Find the next event time (next arrival or process completion)
            int nextEventTime = getNextArrivalTime(currentTime);
            int timeToComplete = selectedProcess.getRemainingTime();
            int executionTime = Math.min(timeToComplete, nextEventTime - currentTime);

            // Execute the process
            selectedProcess.execute(executionTime);
            currentTime += executionTime;

            // If process completed, set completion time
            if (selectedProcess.isCompleted()) {
                selectedProcess.setCompletionTime(currentTime);
            }
        }

        finalizeResults();
        return result;
    }
}