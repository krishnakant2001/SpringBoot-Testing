package codingshuttle.com.TestingAppApplication.repositories;

import codingshuttle.com.TestingAppApplication.TestContainerConfiguration;
import codingshuttle.com.TestingAppApplication.entities.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
@DataJpaTest
@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .name("Pratham")
                .email("pratham@gmail.com")
                .salary(100L)
                .build();
    }
    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnEmployee() {
//        Arrange, Given
        employeeRepository.save(employee);

//        Act, When
        List<Employee> employeesList = employeeRepository.findByEmail(employee.getEmail());

//        Assert, Then
        assertThat(employeesList).isNotNull();
        assertThat(employeesList).isNotEmpty();
        assertThat(employeesList.get(0).getEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    void testFindByEmail_whenEmailIsNotFound_thenReturnEmptyEmployeeList() {
//        Arrange, Given
        String email = "pratham123@gmail.com";

//        Act, When
        List<Employee> employeesList = employeeRepository.findByEmail(email);

//        Assert, Then
        assertThat(employeesList).isNotNull();
        assertThat(employeesList).isEmpty();
    }
}