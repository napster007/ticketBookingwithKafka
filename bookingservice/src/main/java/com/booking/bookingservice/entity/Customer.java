package com.booking.bookingservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="customer")
public class Customer {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    private String email;

    private String address;
}
