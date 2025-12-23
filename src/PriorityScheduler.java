import java.util.*;

class PriorityScheduler extends Scheduler {

    public PriorityScheduler(List<Process> processes, int contextSwitchTime) {
        super(processes, contextSwitchTime);
    }

    @Override
    public SchedulerResult schedule() {
        int currentTime = 0;

        while (!allProcessesCompleted()) {
            List<Process> arrivedProcesses = getArrivedProcesses(currentTime);

            if (arrivedProcesses.isEmpty()) {
                currentTime = getNextArrivalTime(currentTime);
                continue;
            }

            // Select process with highest priority (lowest priority number)
            Process selectedProcess = arrivedProcesses.stream()
                    .min(Comparator.comparingInt(Process::getPriority)
                            .thenComparingInt(Process::getArrivalTime))
                    .orElse(null);

            if (selectedProcess == null)
                break;

            // Apply context switch
            currentTime = performContextSwitch(currentTime, selectedProcess.getName());

            // Add to execution order
            addToExecutionOrder(selectedProcess.getName());

            // Set start time
            selectedProcess.setStartTime(currentTime);

            // Find next event time
            int nextEventTime = getNextArrivalTime(currentTime);
            int timeToComplete = selectedProcess.getRemainingTime();
            int executionTime = Math.min(timeToComplete, nextEventTime - currentTime);

            // Execute process
            selectedProcess.execute(executionTime);
            currentTime += executionTime;

            // Set completion time if done
            if (selectedProcess.isCompleted()) {
                selectedProcess.setCompletionTime(currentTime);
            }
        }

        finalizeResults();
        return result;
    }
}