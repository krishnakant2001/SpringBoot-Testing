package codingshuttle.com.TestingAppApplication.service.impl;

import codingshuttle.com.TestingAppApplication.TestContainerConfiguration;
import codingshuttle.com.TestingAppApplication.dto.EmployeeDto;
import codingshuttle.com.TestingAppApplication.entities.Employee;
import codingshuttle.com.TestingAppApplication.exceptions.ResourceNotFoundException;
import codingshuttle.com.TestingAppApplication.repositories.EmployeeRepository;
import codingshuttle.com.TestingAppApplication.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee mockEmployee;
    private EmployeeDto mockEmployeeDto;

    @BeforeEach
    void setUp() {
        mockEmployee = Employee.builder()
                .id(1L)
                .email("krishnakant@gmail.com")
                .name("Krishnakant")
                .salary(200L)
                .build();

        mockEmployeeDto = modelMapper.map(mockEmployee, EmployeeDto.class);
    }

    @Test
    void testGetEmployeeById_whenEmployeeIdIsPresent_ThenReturnEmployeeDto() {
        //assign
        Long id = mockEmployee.getId();

        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee)); //stubbing

        //act
        EmployeeDto employeeDto = employeeService.getEmployeeById(id);

        //assert
        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getId()).isEqualTo(id);
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployee.getEmail());
        verify(employeeRepository, times(1)).findById(id);

    }

    @Test
    void testGetEmployeeById_whenEmployeeIdIsNotPresent_ThenThrowException() {
        //arrange
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        //act and assert
        assertThatThrownBy(() -> employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
    }

    @Test
    void testCreateNewEmployee_whenValidEmployee_ThenCreateNewEmployee() {
        //assign
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        //act
        EmployeeDto employeeDto = employeeService.createNewEmployee(mockEmployeeDto);

        //assert
        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);

        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployee.getEmail());
//        verify(employeeRepository).save(any(Employee.class));
        verify(employeeRepository).save(employeeArgumentCaptor.capture());

        Employee captureEmployee = employeeArgumentCaptor.getValue();
        assertThat(captureEmployee.getEmail()).isEqualTo(mockEmployee.getEmail());
    }

    @Test
    void testCreateNewEmployee_whenAttemptingToCreateEmployeeWithExistingEmail_thenThrowsException() {
        //arrange
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of(mockEmployee));

        //act and assert
        assertThatThrownBy(() -> employeeService.createNewEmployee(mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: "+ mockEmployeeDto.getEmail());

        verify(employeeRepository).findByEmail(mockEmployeeDto.getEmail());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenValidEmployee_thenUpdateEmployee() {
        //assign
        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDto.setName("Random Name");
        mockEmployeeDto.setSalary(199L);

        Employee newEmployee = modelMapper.map(mockEmployeeDto, Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);

        //act
        EmployeeDto updatedEmployeeDto = employeeService.updateEmployee(mockEmployeeDto.getId(), mockEmployeeDto);

        //assert
        assertThat(updatedEmployeeDto).isEqualTo(mockEmployeeDto);

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any());

    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowsException() {
        //arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        //act and arrange
        assertThatThrownBy(() -> employeeService.updateEmployee(1L, mockEmployeeDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateEmail_thenThrowsException() {
        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDto.setName("Random");
        mockEmployeeDto.setEmail("random@gmail.com");

        //act and assert
        assertThatThrownBy(() -> employeeService.updateEmployee(mockEmployeeDto.getId(), mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository).findById(mockEmployeeDto.getId());
        verify(employeeRepository, never()).save(any());
    }
    @Test
    void testDeleteEmployee_whenEmployeeIsValid_thenDeleteEmployee() {
        //arrange
        when(employeeRepository.existsById(mockEmployeeDto.getId())).thenReturn(true);

        assertThatCode(() -> employeeService.deleteEmployee(mockEmployeeDto.getId()))
                .doesNotThrowAnyException();

        verify(employeeRepository).deleteById(mockEmployeeDto.getId());
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrownException() {
        when(employeeRepository.existsById(mockEmployeeDto.getId())).thenReturn(false);

        //act and assert
        assertThatThrownBy(() -> employeeService.deleteEmployee(mockEmployeeDto.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: " + mockEmployeeDto.getId());

        verify(employeeRepository, never()).deleteById(anyLong());
    }
}