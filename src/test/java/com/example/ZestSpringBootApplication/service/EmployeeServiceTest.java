package com.example.ZestSpringBootApplication.service;

import com.example.ZestSpringBootApplication.entity.Employee;
import com.example.ZestSpringBootApplication.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .id(1)
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .department("Engineering")
                .position("Software Engineer")
                .salary(75000.0)
                .dateOfJoining(LocalDate.of(2023, 1, 15))
                .build();
    }

    @Test
    void saveEmployee_shouldDelegateToRepositoryAndReturnSavedEntity() {
        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.saveEmployee(employee);

        assertThat(result).isEqualTo(employee);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void getEmployeeById_whenFound_shouldReturnEmployee() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1);

        assertThat(result).isEqualTo(employee);
        verify(employeeRepository).findById(1);
    }

    @Test
    void getEmployeeById_whenNotFound_shouldThrow() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getEmployeeById(99))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getAllEmployees_shouldReturnPagedResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(List.of(employee), pageable, 1);
        when(employeeRepository.findAll(pageable)).thenReturn(page);

        Page<Employee> result = employeeService.getAllEmployees(pageable);

        assertThat(result.getContent()).containsExactly(employee);
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(employeeRepository).findAll(pageable);
    }

    @Test
    void deleteEmployee_shouldDelegateToRepository() {
        doNothing().when(employeeRepository).deleteById(1);

        employeeService.deleteEmployee(1);

        verify(employeeRepository, times(1)).deleteById(1);
    }

    @Test
    void updateEmployee_whenFound_shouldUpdateFieldsAndSave() {
        Employee incoming = Employee.builder()
                .name("John Smith")
                .email("john.smith@example.com")
                .department("Marketing")
                .position("Manager")
                .salary(90000.0)
                .dateOfJoining(LocalDate.of(2024, 3, 1))
                .build();

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee result = employeeService.updateEmployee(1, incoming);

        assertThat(result.getName()).isEqualTo("John Smith");
        assertThat(result.getEmail()).isEqualTo("john.smith@example.com");
        assertThat(result.getDepartment()).isEqualTo("Marketing");
        assertThat(result.getPosition()).isEqualTo("Manager");
        assertThat(result.getSalary()).isEqualTo(90000.0);
        assertThat(result.getDateOfJoining()).isEqualTo(LocalDate.of(2024, 3, 1));
        verify(employeeRepository).save(employee);
    }

    @Test
    void updateEmployee_whenNotFound_shouldThrow() {
        when(employeeRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.updateEmployee(42, employee))
                .isInstanceOf(NoSuchElementException.class);

        verify(employeeRepository, never()).save(any());
    }
}
