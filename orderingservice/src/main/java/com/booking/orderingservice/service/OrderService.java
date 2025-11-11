package com.booking.orderingservice.service;

import com.booking.bookingservice.event.BookingEvent;
import com.booking.orderingservice.client.InventoryServiceClient;
import com.booking.orderingservice.entity.Order;
import com.booking.orderingservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    private OrderRepository orderRepository;
    private InventoryServiceClient inventoryServiceClient;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        InventoryServiceClient inventoryServiceClient) {
        this.orderRepository = orderRepository;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    @KafkaListener(topics ="booking", groupId="order-service")
    public void orderEvent(BookingEvent bookingEvent){
        log.info("Recveiver order event: {}", bookingEvent);

        //create order object for DZb
        Order order = createOrder(bookingEvent);
        orderRepository.saveAndFlush(order);
        //update the inventory
        // Update Inventory
        inventoryServiceClient.updateInventory(order.getEventId(), order.getTicketCount());
        log.info("Inventory updated for event: {}, less tickets: {}", order.getEventId(), order.getTicketCount());
    }


    private Order createOrder(BookingEvent bookingEvent) {
        return Order.builder()
                .customerId(bookingEvent.getUserId())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }
}
