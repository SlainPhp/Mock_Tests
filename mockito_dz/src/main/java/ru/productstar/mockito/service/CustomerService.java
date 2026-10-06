package ru.productstar.mockito.service;

import ru.productstar.mockito.model.Customer;
import ru.productstar.mockito.repository.CustomerRepository;

public class CustomerService {

    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getOrCreate(String name) {
        Customer customer = customerRepository.getByName(name);
        if (customer == null) {
            customer = new Customer(name);
            return customerRepository.add(customer);
        } else {
            return customer;
        }
    }

    public Customer getCustomerById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID должен быть положительным");
        }
        Customer customer = customerRepository.getById(id);
        if (customer == null) {
            throw new IllegalStateException("Клиент с ID " + id + " не найден");
        }
        return customer;
    }
}
