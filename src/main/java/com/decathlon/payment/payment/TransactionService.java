package com.decathlon.payment.payment;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final ItemRepository itemRepository;

    public TransactionService(TransactionRepository transactionRepository, ItemRepository itemRepository) {
        this.transactionRepository = transactionRepository;
        this.itemRepository = itemRepository;
    }

    public List<Transaction> getAll() {
        return this.transactionRepository.findAll().stream().map(this::map).toList();
    }

    public Transaction getById(Integer id) throws BadRequestException {
        if(Objects.isNull(id) || id < 1) {
            log.error("INCORRECT_ID : {}", id);
            throw new BadRequestException("INCORRECT_ID");
        }
        Transaction t = this.transactionRepository.findById(id).orElseThrow(() -> new NoSuchElementException("ID_NOT_FOUND : " + id));
        return this.map(t);
    }

    public Transaction create(Transaction transaction) throws BadRequestException {
        // transaction check
        if(Objects.isNull(transaction) || CollectionUtils.isEmpty(transaction.getItems())) {
            log.error("INCORRECT_PARAMETER : {}", transaction);
            throw new BadRequestException("INCORRECT_PARAMETER");
        }
        Float totalAmount = 0F;
        // items check and calcul of total amount
        for(Item item : transaction.getItems()) {
            this.checkItem(item);
            totalAmount += Float.parseFloat(item.getPrice()) * Integer.parseInt(item.getQuantity());
        }

        // transaction save
        Transaction newTransaction = new Transaction();
        newTransaction.setPaymentType(transaction.getPaymentType());
        newTransaction.setPaymentStatus(PaymentStatus.NEW);
        newTransaction.setTotalAmount(String.valueOf(totalAmount));
        newTransaction = this.transactionRepository.save(newTransaction);

        // items save
        List<Item> newItems = new ArrayList<>();
        for(Item item : transaction.getItems()) {
           Item newItem = new Item();
           newItem.setTransaction(newTransaction);
           newItem.setName(item.getName());
           newItem.setPrice(item.getPrice());
           newItem.setQuantity(item.getQuantity());
           newItems.add(newItem);
        }
        newItems = this.itemRepository.saveAll(newItems);
        newTransaction.setItems(newItems);
        return this.map(newTransaction);
    }

    public Transaction updateTransaction(Integer transactionId, Transaction updateTransaction) throws BadRequestException {
        Transaction transaction = this.transactionRepository.findById(transactionId).orElseThrow(() -> new NoSuchElementException("ID_NOT_FOUND : " + transactionId));
        if(
            PaymentStatus.AUTHORIZED.equals(transaction.getPaymentStatus())
            || PaymentStatus.CAPTURED.equals(transaction.getPaymentStatus())
            || PaymentStatus.CANCELED.equals(transaction.getPaymentStatus())
        ) {
            throw new BadRequestException("TRANSACTION_UNMODIFIABLE");
        }
        Float totalAmount = 0F;
        List<Item> newItems = new ArrayList<>();
        // items check and calcul of total amount
        for(Item item : updateTransaction.getItems()) {
            this.checkItem(item);
            totalAmount += Float.parseFloat(item.getPrice()) * Integer.parseInt(item.getQuantity());
            Item newItem = new Item();
            newItem.setTransaction(transaction);
            newItem.setName(item.getName());
            newItem.setPrice(item.getPrice());
            newItem.setQuantity(item.getQuantity());
            newItems.add(newItem);
        }
        this.itemRepository.deleteAll(transaction.getItems());
        this.itemRepository.saveAll(newItems);

        transaction.setPaymentType(updateTransaction.getPaymentType());
        transaction.setTotalAmount(String.valueOf(totalAmount));
        transaction.setItems(newItems);
        transaction = this.transactionRepository.save(transaction);

        return this.map(transaction);
    }

    public Transaction updatePaymentStatus(Integer transactionId, PaymentStatus paymentStatus) throws BadRequestException {
        Transaction transaction = this.transactionRepository.findById(transactionId).orElseThrow(() -> new NoSuchElementException("ID_NOT_FOUND : " + transactionId));
        if(PaymentStatus.CAPTURED.equals(paymentStatus) && !transaction.getPaymentStatus().equals(PaymentStatus.AUTHORIZED)) {
            throw new BadRequestException("NOT_AUTHORIZED_ACTION");
        }
        if(PaymentStatus.CAPTURED.equals(transaction.getPaymentStatus()) || PaymentStatus.CANCELED.equals(transaction.getPaymentStatus())) {
            throw new BadRequestException("CAPTURED_OR_CANCELED_TRANSACTION");
        }
        transaction.setPaymentStatus(paymentStatus);
        transaction = this.transactionRepository.save(transaction);
        return this.map(transaction);
    }

    private void checkItem(Item item) throws BadRequestException {
        if(Objects.isNull(item)
                || StringUtils.isEmpty(item.getName())
                || StringUtils.isEmpty(item.getQuantity())
                || StringUtils.isEmpty(item.getPrice())
        ) {
            log.error("INCORRECT_ITEM : {}", item);
            throw new BadRequestException("INCORRECT_ITEM");
        }
    }

    private Transaction map(Transaction transaction) {
        Transaction result = new Transaction();
        result.setPaymentType(transaction.getPaymentType());
        result.setPaymentStatus(transaction.getPaymentStatus());
        result.setTotalAmount(transaction.getTotalAmount());
        result.setId(transaction.getId());
        result.setItems(
                transaction.getItems().stream().map(i ->
                        {
                            Item item = new Item();
                            item.setId(i.getId());
                            item.setName(i.getName());
                            item.setPrice(i.getPrice());
                            item.setQuantity(i.getQuantity());
                            return item;
                        }

                ).toList()
        );
        return result;
    }

}
