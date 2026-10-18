package stepsword.mahoutsukai.util;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
public final class MahouJobs {
 public static final Map<UUID,JobType> running=new LinkedHashMap<>();
 public static void addJob(UUID owner,JobType job){running.putIfAbsent(owner,job);}
 public static void runJobs(){var pending=new LinkedHashMap<>(running);running.clear();for(var job:pending.values())job.run();}
 public static void clear(){running.clear();}
}
