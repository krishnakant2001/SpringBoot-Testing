package codingshuttle.com.TestingAppApplication.controllers;

import codingshuttle.com.TestingAppApplication.dto.EmployeeDto;
import codingshuttle.com.TestingAppApplication.entities.Employee;
import codingshuttle.com.TestingAppApplication.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class EmployeeControllerTestIT extends AbstractIntegrationTest{

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee testEmployee;

    private EmployeeDto testEmployeeDto;

    @BeforeEach
    void setUp() {

        testEmployee = Employee.builder()
                .email("krishnakant@gmail.com")
                .name("krishnakant")
                .salary(100L)
                .build();

        testEmployeeDto = EmployeeDto.builder()
                .email("krishnakant@gmail.com")
                .name("krishnakant")
                .salary(100L)
                .build();

        employeeRepository.deleteAll();
    }

    @Test
    void testGetEmployeeById_success() {
        // Save the employee and get the generated ID
        Employee savedEmployee = employeeRepository.save(testEmployee);

        // Create expected DTO with the actual generated ID
        EmployeeDto expectedEmployeeDto = EmployeeDto.builder()
                .id(savedEmployee.getId())
                .email(savedEmployee.getEmail())
                .name(savedEmployee.getName())
                .salary(savedEmployee.getSalary())
                .build();

        webTestClient.get()
                .uri("/employee/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .isEqualTo(expectedEmployeeDto);
//                .value(employeeDto -> {
//                    assertThat(employeeDto.getEmail()).isEqualTo(savedEmployee.getEmail());
//                    assertThat(employeeDto.getId()).isEqualTo(savedEmployee.getId());
//                });
    }

    @Test
    void testGetEmployeeById_failure() {
        webTestClient.get()
                .uri("/employee/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeAlreadyExists_thenThrowException() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        webTestClient.post()
                .uri("/employee")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();

    }

    @Test
    void testCreateNewEmployee_whenEmployeeDoesNotExists_thenCreateEmployee() {
        webTestClient.post()
                .uri("/employee")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(testEmployeeDto.getEmail())
                .jsonPath("$.name").isEqualTo(testEmployeeDto.getName())
                .jsonPath("$.salary").isEqualTo(testEmployeeDto.getSalary());
    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        webTestClient.put()
                .uri("/employee/99")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateTheEmail_thenThrowException() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        testEmployeeDto.setName("Random");
        testEmployeeDto.setEmail("random@gmail.com");

        webTestClient.put()
                .uri("/employee/{id}", savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateEmployee_whenEmployeeIsValid_thenUpdateEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        testEmployeeDto.setName("Random");
        testEmployeeDto.setSalary(299L);
        testEmployeeDto.setId(savedEmployee.getId());

        webTestClient.put()
                .uri("/employee/{id}", savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .isEqualTo(testEmployeeDto);
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        webTestClient.delete()
                .uri("/employee/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployee_whenEmployeeExists_thenDeleteEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        webTestClient.delete()
                .uri("/employee/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

        webTestClient.delete()
                .uri("/employee/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isNotFound();
    }
}