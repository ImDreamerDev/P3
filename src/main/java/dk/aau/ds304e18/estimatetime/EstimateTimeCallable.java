package dk.aau.ds304e18.estimatetime;

import dk.aau.ds304e18.math.InverseGaussian;
import dk.aau.ds304e18.math.MonteCarlo;
import dk.aau.ds304e18.models.Task;

import java.util.*;
import java.util.concurrent.Callable;

public class EstimateTimeCallable implements Callable<Estimate> {
    private final List<Task> taskList;
    private final double amountEmployees;
    private final Random random = new Random();
    private final HashMap<Task, InverseGaussian> invG = new HashMap<>();
    private final int repeats;
    
    public EstimateTimeCallable(List<Task> taskList, double amountEmployees, int repeats) {
        this.taskList = new ArrayList<>(taskList);
        this.amountEmployees = amountEmployees;
        this.repeats = repeats;
    }

    public Estimate call() {
        List<Double> chances = new ArrayList<>();
        final int tempBig;
        for (Task task : taskList)
            invG.put(task, task.getInvG());

        if (amountEmployees < taskList.size())
            tempBig = (int) amountEmployees;
        else
            tempBig = taskList.size();

        double duration = 0.0;

        //Repeat repeats time
        for (int i = 0; i < repeats; i++) {
            if (amountEmployees > 1) {
                //Initialize the List with an initial capacity so it doesn't have to resize
                List<Task> tasksDone = new ArrayList<>(taskList.size());

                //Initialize the list with an initial capacity so it doesn't have to resize
                List<Double> durations = new ArrayList<>(tempBig);

                HashMap<Task, Double> taskDoneAt = new HashMap<>();

                for (int j = 0; j < tempBig; j++) {
                    durations.add(0d);
                }

                while (taskList.size() != tasksDone.size()) {
                    for (int j = 0; j < amountEmployees && j < taskList.size(); j++) {
                        if (taskList.size() == tasksDone.size()) break;
                        if (0 + durations.get(j) != 0 + Collections.min(durations) && durations.get(j) != 0) continue;
                        boolean temp = false;
                        for (Task task : taskList) {
                            if (tasksDone.contains(task)) continue;

                            if (tasksDone.containsAll(task.getDependencies())) {
                                boolean skip = false;

                                for (Task dependency : task.getDependencies()) {
                                    if (taskDoneAt.get(dependency) > durations.get(j)) {
                                        skip = true;
                                        break;
                                    }
                                }

                                if (skip)
                                    continue;

                                temp = true;

                                //Calculate the duration at the given random value and add that to duration
                                durations.set(j, durations.get(j) + invG.get(task).getDuration(random.nextDouble() * 100));

                                taskDoneAt.put(task, durations.get(j));
                                tasksDone.add(task);

                                if (!(durations.indexOf(Collections.min(durations)) == j))
                                    break;
                            }
                        }

                        if (!temp) {
                            MonteCarlo.allLowestToNextLowest(durations);
                        }
                    }
                }

                duration += durations.get(durations.indexOf(Collections.max(durations)));

            } else {
                //For each task in the taskList
                for (Task task : taskList) {

                    //Calculate the duration at the given random value and add that to duration
                    double temp = invG.get(task).getDuration(random.nextDouble() * 100);
                    duration += temp;

                }
            }

            try {
                chances.set((int) Math.round((duration / (i + 1))),
                        chances.get((int) Math.round((duration / (i + 1)))) + 1);
            } catch (IndexOutOfBoundsException e) {
                while (chances.size() < (int) Math.round((duration / (i + 1))))
                    chances.add(0d);
                chances.add((int) Math.round((duration / (i + 1))), 1d);
            }

        }
        //Also send the start time of each task back
        return new Estimate(chances, duration);
    }
}
