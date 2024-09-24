package com.decathlon.payment;

import com.decathlon.payment.payment.*;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UnitTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private TransactionService transactionService;

    @Test
    public void testCreateWithNullParam_throwException() {
        Assertions.assertThrows(BadRequestException.class, () -> this.transactionService.create(null));
    }
    @Test
    public void testCreateWithNullItem_throwException() {
        Transaction transaction = new Transaction();
        transaction.setPaymentType(PaymentType.CREDIT_CARD);
        Assertions.assertThrows(BadRequestException.class, () -> this.transactionService.create(transaction));
    }
    @Test
    public void testCreateWithEmptyItem_throwException() {
        Transaction transaction = new Transaction();
        transaction.setPaymentType(PaymentType.CREDIT_CARD);
        transaction.setItems(new ArrayList<>());
        Assertions.assertThrows(BadRequestException.class, () -> this.transactionService.create(transaction));
    }

    @Test
    public void testGetByUnknownId_throwException() {
        Mockito.when(this.transactionRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Assertions.assertThrows(NoSuchElementException.class, () -> this.transactionService.getById(123));
    }
}
