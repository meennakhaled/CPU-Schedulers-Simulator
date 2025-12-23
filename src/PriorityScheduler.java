import java.util.*;

public class PriorityScheduler extends Scheduler {

    private int agingInterval;

    public PriorityScheduler(List<Process> processes,
                             int contextSwitchTime,
                             int agingInterval) {
        super(processes, contextSwitchTime);
        this.agingInterval = agingInterval;
    }

    @Override
    public SchedulerResult schedule() {

        int currentTime = 0;
        Process current = null;
        int lastAgingTime = 0;

        while (!allProcessesCompleted()) {

            List<Process> ready = getArrivedProcesses(currentTime);

            if (ready.isEmpty()) {
                currentTime++;
                continue;
            }

            // ===== Aging =====
            if (currentTime - lastAgingTime >= agingInterval) {
                for (Process p : ready) {
                    if (p != current && !p.isCompleted()) {
                        p.setPriority(Math.max(0, p.getPriority() - 1));
                    }
                }
                lastAgingTime = currentTime;
            }

            // ===== Select =====
            Process selected = ready.stream()
                    .min(Comparator
                            .comparingInt(Process::getPriority)
                            .thenComparingInt(Process::getArrivalTime))
                    .orElse(null);

            if (selected == null) {
                currentTime++;
                continue;
            }

            // ===== Context Switch =====
            if (current != selected) {
                currentTime = performContextSwitch(currentTime, selected.getName());
                addToExecutionOrder(selected.getName());
                current = selected;

                if (current.getStartTime() == -1) {
                    current.setStartTime(currentTime);
                }
            }

            // ===== Execute =====
            current.execute(1);
            currentTime++;

            if (current.isCompleted()) {
                current.setCompletionTime(currentTime);
                current = null;
            }
        }

        finalizeResults();
        return result;
    }
}
