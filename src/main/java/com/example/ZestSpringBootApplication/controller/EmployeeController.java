package com.example.ZestSpringBootApplication.controller;

import com.example.ZestSpringBootApplication.entity.Employee;
import com.example.ZestSpringBootApplication.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    @PostMapping("/save")
    public void saveEmployee(@RequestBody Employee employee){
        employeeService.saveEmployee(employee);
    }

    @GetMapping("/getById/{id}")
    public Employee getEmployeeById(@PathVariable("id") int employeeId){
        return employeeService.getEmployeeById(employeeId);
    }

    @GetMapping("/getAll")
    public List<Employee> getAllEmployees(){
        return employeeService.getAllEmployees();
    }

    @DeleteMapping("/deleteById/{id}")
    public void deleteEmployeeById(@PathVariable("id") int employeeId){
        employeeService.deleteEmployeeById(employeeId);
    }
}
