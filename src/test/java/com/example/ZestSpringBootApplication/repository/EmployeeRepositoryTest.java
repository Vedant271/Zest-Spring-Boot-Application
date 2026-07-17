package com.example.ZestSpringBootApplication.repository;

import com.example.ZestSpringBootApplication.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee buildEmployee(String name, String email) {
        return Employee.builder()
                .name(name)
                .email(email)
                .department("Engineering")
                .position("Developer")
                .salary(60000.0)
                .dateOfJoining(LocalDate.of(2022, 6, 1))
                .build();
    }

    @Test
    void save_shouldPersistEmployeeAndGenerateId() {
        Employee employee = buildEmployee("Alice", "alice@example.com");

        Employee saved = employeeRepository.save(employee);

        assertThat(saved.getId()).isNotNull();
        Employee found = entityManager.find(Employee.class, saved.getId());
        assertThat(found.getName()).isEqualTo("Alice");
    }

    @Test
    void findById_whenExists_shouldReturnEmployee() {
        Employee employee = entityManager.persistAndFlush(buildEmployee("Bob", "bob@example.com"));

        Optional<Employee> result = employeeRepository.findById(employee.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void findById_whenNotExists_shouldReturnEmpty() {
        Optional<Employee> result = employeeRepository.findById(999);

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_withPageable_shouldReturnPagedResults() {
        entityManager.persist(buildEmployee("Carol", "carol@example.com"));
        entityManager.persist(buildEmployee("Dave", "dave@example.com"));
        entityManager.persist(buildEmployee("Erin", "erin@example.com"));
        entityManager.flush();

        Page<Employee> page = employeeRepository.findAll(PageRequest.of(0, 2));

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent()).hasSize(2);
    }

    @Test
    void deleteById_shouldRemoveEmployee() {
        Employee employee = entityManager.persistAndFlush(buildEmployee("Frank", "frank@example.com"));

        employeeRepository.deleteById(employee.getId());
        entityManager.flush();

        assertThat(entityManager.find(Employee.class, employee.getId())).isNull();
    }

    @Test
    void save_withDuplicateEmail_shouldViolateUniqueConstraint() {
        entityManager.persistAndFlush(buildEmployee("Grace", "grace@example.com"));
        Employee duplicate = buildEmployee("Grace2", "grace@example.com");

        assertThatThrownBy(() -> {
            employeeRepository.save(duplicate);
            entityManager.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void save_withNullRequiredField_shouldViolateNotNullConstraint() {
        Employee invalid = buildEmployee("Henry", "henry@example.com");
        invalid.setDepartment(null);

        assertThatThrownBy(() -> {
            employeeRepository.save(invalid);
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }
}
