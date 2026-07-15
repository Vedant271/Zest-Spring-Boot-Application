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

    public void saveEmployee(Employee employee){
        employeeRepository.save(employee);
    }

    public Employee getEmployeeById(int employeeId){
        return employeeRepository.findById(employeeId).get();
    }

    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }

    public void deleteEmployeeById(int employeeId){
        employeeRepository.deleteById(employeeId);
    }
}
