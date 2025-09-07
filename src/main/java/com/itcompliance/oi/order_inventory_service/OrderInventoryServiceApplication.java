package com.itcompliance.oi.order_inventory_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan
public class OrderInventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderInventoryServiceApplication.class, args);
	}

}
