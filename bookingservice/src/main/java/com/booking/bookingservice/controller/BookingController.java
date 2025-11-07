package com.booking.bookingservice.controller;

import com.booking.bookingservice.request.BookingRequest;
import com.booking.bookingservice.response.BookingResponse;
import com.booking.bookingservice.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping( consumes = "application/json", produces = "application/json", path ="/booking")
    public @ResponseBody BookingResponse createBooking(@RequestBody BookingRequest request){

        return bookingService.createBooking(request);
    }
}
