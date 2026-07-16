package com.example.ZestSpringBootApplication.service;

import com.example.ZestSpringBootApplication.entity.Employee;
import com.example.ZestSpringBootApplication.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {
    @Autowired
    EmployeeRepository employeeRepository;

    public Employee saveEmployee(Employee employee){
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(int id){
        return employeeRepository.findById(id).get();
    }

    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }

    public void deleteEmployeeById(int employeeId){
        employeeRepository.deleteById(employeeId);
    }

    public Employee updateEmployee(int id, Employee employee){
        Employee existingEmployee = employeeRepository.findById(id).get();

        existingEmployee.setName(employee.getName());
        existingEmployee.setEmail(employee.getName());
        existingEmployee.setDepartment(employee.getName());
        existingEmployee.setPosition(employee.getPosition());
        existingEmployee.setSalary(employee.getSalary());
        existingEmployee.setDateOfJoining(employee.getDateOfJoining());
        Employee updatedEmployee = employeeRepository.save(existingEmployee);

        return updatedEmployee;
    }
}
