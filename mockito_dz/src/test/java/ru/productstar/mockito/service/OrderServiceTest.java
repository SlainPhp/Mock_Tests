package ru.productstar.mockito.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.productstar.mockito.ProductNotFoundException;
import ru.productstar.mockito.model.*;
import ru.productstar.mockito.repository.OrderRepository;
import ru.productstar.mockito.repository.ProductRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock private CustomerService customerService;
    @Mock private WarehouseService warehouseService;
    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks private OrderService orderService;

    @BeforeEach
    void setUp() {
        Mockito.reset(customerService, warehouseService, orderRepository, productRepository);
    }

    @Test
    void addProduct_normalDelivery_success() throws ProductNotFoundException {
        Order order = new Order(new Customer("Ivan"));
        order.setId(1);
        Warehouse warehouse = new Warehouse("Main WH", 10);
        Product productForStock = new Product("Book");
        Stock stock = new Stock(productForStock, 500, 10);
        Product product = new Product("Book");

        when(warehouseService.getStock(eq(warehouse), eq("Book"))).thenReturn(stock);

        when(warehouseService.findWarehouse("Book", 1)).thenReturn(warehouse);
        when(productRepository.getByName("Book")).thenReturn(product);
        when(orderRepository.addDelivery(eq(1), any(Delivery.class))).thenReturn(order);

        Order result = orderService.addProduct(order, "Book", 1, false);

        assertNotNull(result, "Результат добавления товара не должен быть null");

        verify(warehouseService, times(1)).getStock(eq(warehouse), eq("Book"));
        verify(warehouseService, never()).findClosestWarehouse(anyString(), anyInt());
    }
    @Test
    void addProduct_fastestDelivery_success() throws ProductNotFoundException {
        Order order = new Order(new Customer("Ivan"));
        order.setId(1);

        Warehouse warehouse = new Warehouse("Closest WH", 5);

        Product productForStock = new Product("Phone");
        Stock stock = new Stock(productForStock, 1000, 5);

        Product product = new Product("Phone");


        when(warehouseService.findClosestWarehouse("Phone", 1)).thenReturn(warehouse);
        when(warehouseService.getStock(eq(warehouse), eq("Phone"))).thenReturn(stock);

        when(productRepository.getByName("Phone")).thenReturn(product);
        when(orderRepository.addDelivery(eq(1), any(Delivery.class))).thenReturn(order);

        Order result = orderService.addProduct(order, "Phone", 1, true);

        assertNotNull(result, "Результат не должен быть null");

        verify(warehouseService, times(1)).findClosestWarehouse("Phone", 1);
        verify(warehouseService, never()).findWarehouse(anyString(), anyInt());

        verify(warehouseService, times(1)).getStock(eq(warehouse), eq("Phone"));
    }
}
