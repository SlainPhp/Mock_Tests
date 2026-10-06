package ru.productstar.mockito.model;

public class WarehouseStock{
    private int price;
    private String productName;

    public WarehouseStock(int price, String productName) {
        this.price = price;
        this.productName = productName;
    }

    public int getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }
}
