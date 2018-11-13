package dk.aau.ds304e18.sequence;

import dk.aau.ds304e18.math.InverseGaussian;
import dk.aau.ds304e18.models.Task;

import java.util.*;
import java.util.concurrent.Callable;

public class EstimateTimeCallable implements Callable<List<List<Double>>> {
    private List<Task> taskList;
    private double amountEmployees;
    private int numOfThreads;
    private int numOfMonte;
    private Random random = new Random();
    private InverseGaussian invG = new InverseGaussian();

    public EstimateTimeCallable(List<Task> taskList, double amountEmployees, int numOfThreads, int numOfMonte) {
        this.taskList = taskList;
        this.amountEmployees = amountEmployees;
        this.numOfThreads = numOfThreads;
        this.numOfMonte = numOfMonte;
    }

    public List<List<Double>> call() {
        List<List<Double>> result = new ArrayList<>();
        List<Double> durationList = new ArrayList<>();
        List<Double> chances = new ArrayList<>();

        double duration = 0.0;
        int repeats = numOfMonte / numOfThreads;
        //Repeat repeats time
        for (int i = 0; i < repeats; i++) {
            if (amountEmployees > 1) {
                List<Task> tasksDone = new ArrayList<>();
                List<Double> durations = new ArrayList<>();
                HashMap<Task, Double> taskDoneAt = new HashMap<>();

                for (int j = 0; j < amountEmployees; j++) {
                    durations.add(0d);
                }

                while (taskList.size() != tasksDone.size()) {
                    for (int j = 0; j < amountEmployees; j++) {
                        if (0 + durations.get(j) != 0 + Collections.min(durations)) continue;
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
                                double rand = random.nextDouble() * 100;

                                //Create an inverse gaussian distribution for the task
                                invG.setParams(task.getEstimatedTime(), task.getLambda());

                                //Calculate the duration at the given random value and add that to duration
                                durations.set(j, durations.get(j) + invG.getDuration(rand));

                                taskDoneAt.put(task, durations.get(j));
                                tasksDone.add(task);

                                if (!(durations.indexOf(Collections.min(durations)) == j))
                                    break;
                            }
                        }

                        if (!temp) {
                            List<Double> temp2 = new ArrayList<>();
                            List<Integer> temp3 = new ArrayList<>();
                            double minUpper;

                            double tempDuration = Collections.min(durations);

                            for (Double tempDur : durations) {
                                if (tempDur > tempDuration)
                                    temp2.add(tempDur);
                                else
                                    temp3.add(durations.indexOf(tempDur));
                            }

                            minUpper = Collections.min(temp2);

                            for (Integer tempIndex : temp3)
                                durations.set(tempIndex, minUpper);
                        }
                    }
                }

                duration += durations.get(durations.indexOf(Collections.max(durations)));

            } else {
                //For each task in the taskList
                for (Task task : taskList) {

                    //Create a random double between 0 and 100
                    double rand = random.nextDouble() * 100;

                    //Create an inverse gaussian distribution for the task
                    invG.setParams(task.getEstimatedTime(), task.getLambda());

                    //Calculate the duration at the given random value and add that to duration
                    duration += invG.getDuration(rand);
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
        durationList.add(duration);
        result.add(durationList);
        result.add(chances);
        return result;
    }
}
