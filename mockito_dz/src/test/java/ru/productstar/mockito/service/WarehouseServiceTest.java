package ru.productstar.mockito.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.productstar.mockito.model.Product;
import ru.productstar.mockito.model.Stock;
import ru.productstar.mockito.model.Warehouse;
import ru.productstar.mockito.repository.WarehouseRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private WarehouseService warehouseService;

    @BeforeEach
    void setUp() {
        Mockito.reset(warehouseRepository);
    }


     //Цель: создание трёх складов для тестов

    private List<Warehouse> createTestWarehouses() {
        Product laptop = new Product("Laptop");
        Product phone = new Product("Phone");

        Warehouse wh1 = new Warehouse("WH-1", 10);
        wh1.addStock(new Stock(laptop, 50000, 10));
        wh1.addStock(new Stock(phone, 30000, 5));

        Warehouse wh2 = new Warehouse("WH-2", 30);
        wh2.addStock(new Stock(laptop, 55000, 3));
        wh2.addStock(new Stock(phone, 28000, 20));

        Warehouse wh3 = new Warehouse("WH-3", 50);
        wh3.addStock(new Stock(phone, 27000, 50));

        return Arrays.asList(wh1, wh2, wh3);
    }

    //Цель: поиск существующего товара

    @Test
    void findWarehouse_sufficientQuantity_returnsFirstWarehouse() {
        when(warehouseRepository.all()).thenReturn(createTestWarehouses());

        Warehouse result = warehouseService.findWarehouse("Laptop", 5);

        assertNotNull(result);
        assertEquals("WH-1", result.getName());
        verify(warehouseRepository, times(1)).all();
    }


     //Цель: Поиск несуществующего товара


    @Test
    void findWarehouse_nonExistentProduct_returnsNull() {
        when(warehouseRepository.all()).thenReturn(createTestWarehouses());

        Warehouse result = warehouseService.findWarehouse("Tablet", 1);

        assertNull(result);
        verify(warehouseRepository, times(1)).all();
    }


    //Цель: Поиск существующего товара с недостаточным количеством

    @Test
    void findWarehouse_insufficientQuantity_returnsNull() {
        when(warehouseRepository.all()).thenReturn(createTestWarehouses());

        Warehouse result = warehouseService.findWarehouse("Laptop", 15);

        assertNull(result);
        verify(warehouseRepository, times(1)).all();
    }


    //Цель : Поиск ближайшего склада

    @Test
    void findClosestWarehouse_returnsNearestByDistance() {
        when(warehouseRepository.all()).thenReturn(createTestWarehouses());

        Warehouse result = warehouseService.findClosestWarehouse("Phone", 1);

        assertNotNull(result);
        assertEquals("WH-1", result.getName());
        assertEquals(10, result.getDistance());
        verify(warehouseRepository, times(1)).all();
    }


     //Цель: Граничный случай — нулевой запас.

    @Test
    void findWarehouse_zeroStock_returnsNull() {
        Product tablet = new Product("Tablet");
        Stock zeroStock = new Stock(tablet, 20000, 0);
        Warehouse wh = new Warehouse("WH-Empty", 10);
        wh.addStock(zeroStock);

        when(warehouseRepository.all()).thenReturn(Arrays.asList(wh));

        Warehouse result = warehouseService.findWarehouse("Tablet", 1);

        assertNull(result, "Склад с нулевым запасом не должен быть возвращён");
        verify(warehouseRepository, times(1)).all();
    }
}
