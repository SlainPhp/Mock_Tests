package ru.productstar.mockito.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.productstar.mockito.model.Customer;
import ru.productstar.mockito.repository.CustomerRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        Mockito.reset(customerRepository);
    }
    @Test
    void addCustomerTest() {
        //Цель: проверить создание заказчика
        Customer input = new Customer("Andrey");
        Customer expected = new Customer("Andrey");
        expected.setId(1);

        when(customerRepository.add(any(Customer.class))).thenReturn(expected);
        Customer result = customerRepository.add(input);
        assertEquals(expected, result);
        verify(customerRepository, times(1)).add(any(Customer.class));
    }
    @Test
    void getCustomerById_zeroId_throwsException() {
        // Цель: проверка на ненулевое значение id
        assertThrows(IllegalArgumentException.class, () -> {
            customerService.getCustomerById(0);
        });
        verify(customerRepository, never()).getById(anyInt());
    }
    @Test
    void getCustomerById_notFound_throwsException() {
        int nonExistentId = 999;
        when(customerRepository.getById(nonExistentId)).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> {
            customerService.getCustomerById(nonExistentId);
        });

        verify(customerRepository, times(1)).getById(eq(nonExistentId));
    }
}
