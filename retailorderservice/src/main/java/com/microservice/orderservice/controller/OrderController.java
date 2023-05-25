 Controller Layer
package com.microservice.orderservice.controller;

import com.microservice.orderservice.payload.request.OrderRequest;
import com.microservice.orderservice.payload.response.OrderResponse;
import com.microservice.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Log4j2
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
	private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);
    private AtomicLong id = new AtomicLong();
    private KafkaTemplate<Long, Order> template;
    private StreamsBuilderFactoryBean kafkaStreamsFactory;
    private OrderGeneratorService orderGeneratorService;

    @PostMapping("/placeorder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {

        log.info("OrderController | placeOrder is called");

        log.info("OrderController | placeOrder | orderRequest: {}", orderRequest.toString());

        long orderId = orderService.placeOrder(orderRequest);
        log.info("Order Id: {}", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable long orderId) {

        log.info("OrderController | getOrderDetails is called");

        OrderResponse orderResponse
                = orderService.getOrderDetails(orderId);

        log.info("OrderController | getOrderDetails | orderResponse : " + orderResponse.toString());

        return new ResponseEntity<>(orderResponse,
                HttpStatus.OK);
    }
	
	

    public OrderController(KafkaTemplate<Long, Order> template,
                           StreamsBuilderFactoryBean kafkaStreamsFactory,
                           OrderGeneratorService orderGeneratorService) {
        this.template = template;
        this.kafkaStreamsFactory = kafkaStreamsFactory;
        this.orderGeneratorService = orderGeneratorService;
    }

    @PostMapping
    public Order create(@RequestBody Order order) {
        order.setId(id.incrementAndGet());
        template.send("orders", order.getId(), order);
        LOG.info("Sent: {}", order);
        return order;
    }

    @PostMapping("/generate")
    public boolean create() {
        orderGeneratorService.generate();
        return true;
    }
}