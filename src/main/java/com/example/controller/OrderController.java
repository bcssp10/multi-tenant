package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Order;
import com.example.repository.OrderRepository;

import java.sql.Date;



@RestController
@Transactional
public class OrderController {

	@Autowired
    private OrderRepository orderRepository;
		
	@RequestMapping(path = "/orders", method= RequestMethod.POST)
    public ResponseEntity<?> createSampleOrder() {
	
        Order newOrder = new Order(new Date(System.currentTimeMillis()));
        orderRepository.save(newOrder);       
        return ResponseEntity.ok(newOrder);
		
    }
	
}
