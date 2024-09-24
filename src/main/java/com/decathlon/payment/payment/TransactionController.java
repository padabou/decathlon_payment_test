package com.decathlon.payment.payment;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("transaction")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {
        return ResponseEntity.ok(this.transactionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable("id") Integer id) {
        try {
            return ResponseEntity.ok(this.transactionService.getById(id));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e2) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping()
    public ResponseEntity<Transaction> create(@RequestBody Transaction transaction) {
        try {
            return ResponseEntity.ok(this.transactionService.create(transaction));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable("id") Integer id, @RequestBody Transaction transaction) {
        try {
            return ResponseEntity.ok(this.transactionService.updateTransaction(id, transaction));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e2) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable("id") Integer id, @RequestBody PaymentStatusVo paymentStatus) {
        try {
            return ResponseEntity.ok(this.transactionService.updatePaymentStatus(id, paymentStatus.getPaymentStatus()));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e2) {
            return ResponseEntity.notFound().build();
        }
    }
}
