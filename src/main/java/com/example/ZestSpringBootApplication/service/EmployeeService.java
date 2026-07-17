package com.example.ZestSpringBootApplication.service;

import com.example.ZestSpringBootApplication.entity.Employee;
import com.example.ZestSpringBootApplication.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public Employee saveEmployee(Employee employee){
        return employeeRepository.save(employee);
    }

    public Employee getEmployeeById(int id){
        return employeeRepository.findById(id).get();
    }

    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    public void deleteEmployee(int id){
        employeeRepository.deleteById(id);
    }

    public Employee updateEmployee(int id, Employee employee){
        Employee existingEmployee = employeeRepository.findById(id).get();

        existingEmployee.setName(employee.getName());
        existingEmployee.setEmail(employee.getEmail());
        existingEmployee.setDepartment(employee.getDepartment());
        existingEmployee.setPosition(employee.getPosition());
        existingEmployee.setSalary(employee.getSalary());
        existingEmployee.setDateOfJoining(employee.getDateOfJoining());
        Employee updatedEmployee = employeeRepository.save(existingEmployee);

        return updatedEmployee;
    }
}
