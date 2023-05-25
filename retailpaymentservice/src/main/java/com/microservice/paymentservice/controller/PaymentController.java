package com.microservice.paymentservice.controller;

import com.microservice.paymentservice.payload.PaymentRequest;
import com.microservice.paymentservice.payload.PaymentResponse;
import com.microservice.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.kafka.annotation.KafkaListener;
import com.microservice.orderservice.Order;
import com.microservice.orderservice.service.Order;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@Log4j2
@EnableKafka
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest) {

        log.info("PaymentController | doPayment is called");

        log.info("PaymentController | doPayment | paymentRequest : " + paymentRequest.toString());

        return new ResponseEntity<>(
                paymentService.doPayment(paymentRequest),
                HttpStatus.OK
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable long orderId) {

        log.info("PaymentController | doPayment is called");

        log.info("PaymentController | doPayment | orderId : " + orderId);

        return new ResponseEntity<>(
                paymentService.getPaymentDetailsByOrderId(orderId),
                HttpStatus.OK
        );
    }
	
	
	@Autowired
    OrderManageService orderManageService;

    @KafkaListener(id = "orders", topics = "orders", groupId = "payment")
    public void onEvent(Order o) {
        LOG.info("Received: {}" , o);
        if (o.getStatus().equals("NEW"))
            orderManageService.reserve(o);
        else
            orderManageService.confirm(o);
    }

    @Autowired
    private CustomerRepository repository;

    @PostConstruct
    public void generateData() {
        Random r = new Random();
        Faker faker = new Faker();
        for (int i = 0; i < 100; i++) {
            int count = r.nextInt(100, 1000);
            Customer c = new Customer(null, faker.name().fullName(), count, 0);
            repository.save(c);
        }
    }
}
