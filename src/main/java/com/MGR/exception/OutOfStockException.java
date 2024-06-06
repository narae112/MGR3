package com.MGR.exception;

public class OutOfStockException extends RuntimeException{
    public OutOfStockException(String message) {
        super(message);
    }
    // 상품의 주문수량보다 재고의 수가 적을 때 발생시킬 exception 정의
}
