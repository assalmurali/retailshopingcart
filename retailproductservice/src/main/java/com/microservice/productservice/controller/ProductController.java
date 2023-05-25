package com.microservice.productservice.controller;

import com.microservice.productservice.payload.request.ProductRequest;
import com.microservice.productservice.payload.response.ProductResponse;
import com.microservice.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Log4j2
public class ProductController {

    private final ProductService productService;
	
	@Autowired
	private CircuitBreakerFactory circuitBreakerFactory;
	
	@Autowired
	private HttpBinService httpBin;
	
	
	public ProductController(CircuitBreakerFactory circuitBreakerFactory, HttpBinService httpBin) {
		super();
		this.circuitBreakerFactory = circuitBreakerFactory;
		this.httpBin = httpBin;
	}
	
	

    @PostMapping
    public ResponseEntity<Long> addProduct(@RequestBody ProductRequest productRequest) {

        log.info("ProductController | addProduct is called");

        log.info("ProductController | addProduct | productRequest : " + productRequest.toString());

        long productId = productService.addProduct(productRequest);
        return new ResponseEntity<>(productId, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable("id") long productId) {

        log.info("ProductController | getProductById is called");

        log.info("ProductController | getProductById | productId : " + productId);

        ProductResponse productResponse
                = productService.getProductById(productId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }
	@GetMapping("/get")
	public Map get() {
		return httpBin.get();
	}
	
	@GetMapping("/delay/{seconds}")
	public Map delay(@PathVariable int seconds ) {
		return circuitBreakerFactory.create("delay").run(httpBin.delaySupplier(seconds), t -> {
			LOG.warn("delay service call failed error", t);
			Map<String, String> fallback = new HashMap<>();
			
			fallback.put("Message_from_fallback_functionality_ProductService ", "TVS India");
			
			return fallback;
		});
	}

    @PutMapping("/reduceQuantity/{id}")
    public ResponseEntity<Void> reduceQuantity(
            @PathVariable("id") long productId,
            @RequestParam long quantity
    ) {

        log.info("ProductController | reduceQuantity is called");

        log.info("ProductController | reduceQuantity | productId : " + productId);
        log.info("ProductController | reduceQuantity | quantity : " + quantity);

        productService.reduceQuantity(productId,quantity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteProductById(@PathVariable("id") long productId) {
        productService.deleteProductById(productId);
    }
	
	 @Autowired
     OrderManageService orderManageService;

    @KafkaListener(id = "orders", topics = "orders", groupId = "Product")
    public void onEvent(Order o) {
        LOG.info("Received: {}" , o);
        if (o.getStatus().equals("NEW"))
            orderManageService.reserve(o);
        else
            orderManageService.confirm(o);
    }

    @Autowired
    private ProductRepository repository;

    @PostConstruct
    public void generateData() {
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            int count = r.nextInt(10, 1000);
            Product p = new Product(null, "Product" + i, count, 0);
            repository.save(p);
        }
    }
}
}
