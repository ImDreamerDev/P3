package dk.aau.ds304e18.database;

import dk.aau.ds304e18.models.Employee;
import dk.aau.ds304e18.models.Project;
import dk.aau.ds304e18.models.Task;

class Converter {
    static Project convertProject(DatabaseProject dbProj) {
        Project project = new Project(dbProj.name);
        project.setId(dbProj.id);
        project.setState(dbProj.state);
        //TODO
        return project;
    }

    static Task convertTask(DatabaseTask dbTask) {
        return new Task(dbTask.id, dbTask.name, dbTask.estimatedTime, dbTask.startDate, dbTask.endDate, dbTask.priority);
    }

    static Employee convertEmployee(DatabaseEmployee dbEmp) {
        Employee emp = new Employee(dbEmp.name);
        emp.setId(dbEmp.id);
        return emp;
    }
}
