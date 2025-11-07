package com.booking.bookingservice.service;

import com.booking.bookingservice.client.InventoryServiceClient;
import com.booking.bookingservice.entity.Customer;
import com.booking.bookingservice.repository.CustomerRepository;
import com.booking.bookingservice.request.BookingRequest;
import com.booking.bookingservice.response.BookingResponse;
import com.booking.bookingservice.response.InventoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final CustomerRepository customerRepository;
    private final InventoryServiceClient inventoryServiceClient;

    @Autowired
    public BookingService(final CustomerRepository customerRepository,
                          final InventoryServiceClient inventoryServiceClient){
        this.customerRepository = customerRepository;
        this.inventoryServiceClient = inventoryServiceClient;
    }

    public BookingResponse createBooking(final BookingRequest request){

        //check if the customer exist
        final Customer customer = customerRepository.findById(request.getUserId()).orElse(null);
        if(customer == null){
            throw new RuntimeException("USer not found");
        }
        // check there is enough ticket in the inventory
        final InventoryResponse inventoryResponse = inventoryServiceClient.getInventory(request.getEventId());
        System.out.println("Inventory Service Response: " + inventoryResponse);
        //Check ticket available
        if(inventoryResponse.getCapacity() < request.getTicketCount()){
            throw new RuntimeException("Not enough ticket in inventory");
        }
        //Create booking


        return BookingResponse.builder().build();
    }
}
