package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.math.InverseGaussian;
import dk.aau.ds304e18.models.Task;

import java.util.*;

public class EstimateTimeCallable extends javafx.concurrent.Task<Double> {
    private List<Task> taskList;
    private int amountEmployees;
    private int numOfThreads;
    private int numOfMonte;

    public EstimateTimeCallable(List<Task> taskList, int amountEmployees, int numOfThreads, int numOfMonte) {
        this.taskList = taskList;
        this.amountEmployees = amountEmployees;
        this.numOfThreads = numOfThreads;
        this.numOfMonte = numOfMonte;
    }

    public Double call() {
        Random r = new Random();
        double duration = 0.0;
        int repeats = numOfMonte / numOfThreads;
        //Repeat repeats time
        for (int i = 0; i < repeats; i++) {
            if (isCancelled())
                return 0.0;
            if (amountEmployees > 1) {
                List<Task> tasksDone = new ArrayList<>();
                List<Double> durations = new ArrayList<>();
                HashMap<Task, Double> taskDoneAt = new HashMap<>();

                for (int j = 0; j < amountEmployees; j++) {
                    durations.add(0d);
                }

                while (taskList.size() != tasksDone.size()) {
                    for (int j = 0; j < amountEmployees; j++) {
                        if (!(0 + durations.get(j) == 0 + Collections.min(durations))) continue;
                        boolean temp = false;
                        for (Task task : taskList) {
                            if (tasksDone.contains(task)) continue;

                            if (tasksDone.containsAll(task.getDependencies())) {

                                boolean skip = false;

                                for (Task dependency : task.getDependencies()) {
                                    if (taskDoneAt.get(dependency) > durations.get(j))
                                        skip = true;
                                }

                                if (skip)
                                    continue;

                                temp = true;

                                //Create a random double between 0 and 100
                                double rand = r.nextDouble() * 100;

                                //Create an inverse gaussian distribution for the task
                                InverseGaussian invG = new InverseGaussian(task.getEstimatedTime(), task.getLambda());

                                //Calculate the duration at the given random value and add that to duration
                                durations.set(j, durations.get(j) + invG.getDuration(rand));

                                taskDoneAt.put(task, durations.get(j));
                                tasksDone.add(task);

                                if (!(durations.indexOf(Collections.min(durations)) == j))
                                    break;

                            }
                        }

                        if (!temp) {
                            int temp2 = durations.indexOf(Collections.min(durations));
                            List<Double> tempDurations = new ArrayList<>();
                            for (Double d : durations) {
                                if (d > durations.get(temp2))
                                    tempDurations.add(d);
                            }
                            if (tempDurations.size() != 0)
                                durations.set(temp2, 0 + Collections.min(tempDurations));
                        }
                    }
                }

                duration += durations.get(durations.indexOf(Collections.max(durations)));

            } else {
                //For each task in the taskList
                for (Task task : taskList) {

                    //Create a random double between 0 and 100
                    double rand = r.nextDouble() * 100;

                    //Create an inverse gaussian distribution for the task
                    InverseGaussian invG = new InverseGaussian(task.getEstimatedTime(), task.getLambda());

                    //Calculate the duration at the given random value and add that to duration
                    duration += invG.getDuration(rand);
                }
            }
            updateProgress(i, repeats);
        }
        return duration;
    }
}