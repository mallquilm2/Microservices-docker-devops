/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.paymentchain.transactions.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.paymentchain.transactions.entities.Transaction;
import com.paymentchain.transactions.repository.TransactionRepository;

/**
 *
 * @author sotobotero
 */
@RestController
@RequestMapping("/transaction")
public class TransactionRestController {
    
    @Autowired
    TransactionRepository transactionRepository;
    
      
    @GetMapping()
    public List<Transaction> list() {
        return transactionRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> get(@PathVariable long id) {
         return transactionRepository.findById(id).map(x -> ResponseEntity.ok(x)).orElse(ResponseEntity.notFound().build());      
    }
    
    @GetMapping("/transactions")
    public List<Transaction> get(@RequestParam String ibanAccount) {
      return transactionRepository.findByIbanAccount(ibanAccount);      
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable String id, @RequestBody Transaction input) {
    	input = validarTransaction(input);
    	Transaction save = transactionRepository.save(input);
    	return ResponseEntity.ok(save);
    }
    
    @PostMapping
    public ResponseEntity<?> post(@RequestBody Transaction input) {
        input = validarTransaction(input);
    	Transaction save = transactionRepository.save(input);
        return ResponseEntity.ok(save);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
    	Transaction transaction = transactionRepository.findById(Long.parseLong(id)).get();
    	transactionRepository.delete(transaction);
    	return (ResponseEntity<?>) ResponseEntity.status(HttpStatus.OK);
    }
    
    public Transaction validarTransaction(Transaction input) {
    	Date date = Date.from(input.getDate().atZone(ZoneId.systemDefault()).toInstant());
    	if(input.getFee()>0) {
    		input.setAmount(input.getAmount()-input.getFee());
    	}
    	if(date.after(new Date())) {
    		input.setStatus("PENDIENTE");
    	}else {
    		input.setStatus("LIQUIDADO");
    	}
    	return input;
    }
    
}
