package com.microservice.paymentservice.repository;

import com.microservice.paymentservice.model.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import pl.piomin.payment.domain.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
}