
import java.util.List;

public interface scheduler2 {
    SchedulerResult schedule(List<Process> processes, int contextSwitchTime);
}