package dk.aau.ds304e18.database;

import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

class Converter {
    static Project convertProject(DatabaseProject dbProj) {
        return new Project(dbProj);
    }

    static Task convertTask(DatabaseTask dbTask) {
        return new Task(dbTask);
    }

    static Employee convertEmployee(DatabaseEmployee dbEmp) {
        return new Employee(dbEmp);
    }
}
