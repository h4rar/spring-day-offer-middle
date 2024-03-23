package com.onedayoffer.taskdistribution.services;

import com.onedayoffer.taskdistribution.DTO.EmployeeDTO;
import com.onedayoffer.taskdistribution.DTO.TaskDTO;
import com.onedayoffer.taskdistribution.DTO.TaskStatus;
import com.onedayoffer.taskdistribution.repositories.EmployeeRepository;
import com.onedayoffer.taskdistribution.repositories.TaskRepository;
import com.onedayoffer.taskdistribution.repositories.entities.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.*;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final TaskRepository taskRepository;

    private final ModelMapper modelMapper;

    public List<EmployeeDTO> getEmployees(@Nullable String sortDirection) {
        List<Employee> employees = getEmployeeList(sortDirection);
        Type listType = new TypeToken<List<EmployeeDTO>>() {
        }.getType();
        return modelMapper.map(employees, listType);
    }

    private List<Employee> getEmployeeList(String sortDirection) {
        List<Employee> employees;
        if (StringUtils.isNotBlank(sortDirection)) {
            Sort.Direction direction = Sort.Direction.fromString(sortDirection);
            employees = employeeRepository.findAllAndSort(Sort.by(direction, "fio"));
        } else {
            employees = employeeRepository.findAll();
        }
        return employees;
    }

    @Transactional
    public EmployeeDTO getOneEmployee(Integer id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            return null;
        }
        Type listType = new TypeToken<EmployeeDTO>() {
        }.getType();
        return modelMapper.map(employee.get(), listType);
    }

    @Transactional
    public List<TaskDTO> getTasksByEmployeeId(Integer id) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            return List.of();
        }
        Employee employee = employeeOpt.get();
        List<Task> tasks = employee.getTasks();
        Type listType = new TypeToken<List<TaskDTO>>() {
        }.getType();
        return modelMapper.map(tasks, listType);
    }

    @Transactional
    public void changeTaskStatus(Integer taskId, TaskStatus status) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isEmpty()) {
            throw new EntityNotFoundException();
        }
        task.get().setStatus(status);
    }

    @Transactional
    public void postNewTask(Integer employeeId, TaskDTO newTask) {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        if (employee.isEmpty()) {
            throw new EntityNotFoundException();
        }
        Type listType = new TypeToken<Task>() {
        }.getType();
        Task task = modelMapper.map(newTask, listType);
        task.setEmployee(employee.get());
        employee.get().getTasks().add(task);
    }
}
