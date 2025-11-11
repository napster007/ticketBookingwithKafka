package com.booking.bookingservice.service;

import com.booking.bookingservice.client.InventoryServiceClient;
import com.booking.bookingservice.entity.Customer;
import com.booking.bookingservice.event.BookingEvent;
import com.booking.bookingservice.repository.CustomerRepository;
import com.booking.bookingservice.request.BookingRequest;
import com.booking.bookingservice.response.BookingResponse;
import com.booking.bookingservice.response.InventoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class BookingService {

    private final CustomerRepository customerRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    @Autowired
    public BookingService(final CustomerRepository customerRepository,
                          final InventoryServiceClient inventoryServiceClient, KafkaTemplate<String, BookingEvent> kafkaTemplate){
        this.customerRepository = customerRepository;
        this.inventoryServiceClient = inventoryServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public BookingResponse createBooking(final BookingRequest request){

        log.info("Booking Info: {}", request);
        //check if the customer exist
        final Customer customer = customerRepository.findById(request.getUserId()).orElse(null);
        if(customer == null){
            throw new RuntimeException("USer not found");
        }
        // check there is enough ticket in the inventory
        final InventoryResponse inventoryResponse = inventoryServiceClient.getInventory(request.getEventId());
       log.info("Inventory Service Response: " + inventoryResponse);
        //Check ticket available
        if(inventoryResponse.getCapacity() < request.getTicketCount()){
            throw new RuntimeException("Not enough ticket in inventory");
        }
        //Create booking
        final BookingEvent bookingEvent = createBookingEvent(request,customer,inventoryResponse);
        //send booking event to Kafka topic
        kafkaTemplate.send("booking", bookingEvent);
        log.info("Booking sent to the Kafka {}", bookingEvent);


        return BookingResponse.builder()
                .userId(bookingEvent.getUserId())
                .eventId(bookingEvent.getEventId())
                .ticketCount(bookingEvent.getTicketCount())
                .totalPrice(bookingEvent.getTotalPrice())
                .build();
    }

    private BookingEvent createBookingEvent(final BookingRequest request,
                                            final Customer customer,
                                            final InventoryResponse inventoryResponse){

        log.info("Booking INFO {}", request);
        log.info("Customer INFO: {}", customer);
        log.info("Inventory Info: {}", inventoryResponse);
        return BookingEvent.builder()
                .userId(customer.getId())
                .eventId(request.getEventId())
                .ticketCount(request.getTicketCount())
                .totalPrice(inventoryResponse.getTicketPrice().multiply(BigDecimal.valueOf(request.getTicketCount())))
                .build();
    }
}
