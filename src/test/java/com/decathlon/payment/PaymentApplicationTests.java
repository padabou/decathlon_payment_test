package com.decathlon.payment;

import com.decathlon.payment.payment.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Locale;

@Slf4j
@SpringBootTest
public class PaymentApplicationTests {

	@Autowired
	private TransactionService transactionService;

	@Test
	public void testSuite() throws BadRequestException {

		/** Test suite first part */
		Transaction transaction = new Transaction();
		transaction.setPaymentType(PaymentType.CREDIT_CARD);
		Item item = new Item();
		item.setName("T-shirts");
		item.setQuantity("5");
		item.setPrice("19.99");
		transaction.setItems(List.of(item));
		Transaction result = this.transactionService.create(transaction);
		Assertions.assertNotNull(result);
		Assertions.assertNotNull(result.getItems());
		Assertions.assertEquals(PaymentStatus.NEW, result.getPaymentStatus());
		Assertions.assertEquals("99.95", result.getTotalAmount());

		Transaction resultStep2 = this.transactionService.updatePaymentStatus(result.getId(), PaymentStatus.AUTHORIZED);
		Assertions.assertNotNull(resultStep2);
		Assertions.assertNotNull(resultStep2.getItems());
		Assertions.assertEquals(PaymentStatus.AUTHORIZED, resultStep2.getPaymentStatus());

		Transaction resultStep3 = this.transactionService.updatePaymentStatus(result.getId(), PaymentStatus.CAPTURED);
		Assertions.assertNotNull(resultStep3);
		Assertions.assertNotNull(resultStep3.getItems());
		Assertions.assertEquals(PaymentStatus.CAPTURED, resultStep3.getPaymentStatus());


		/** Test suite second part */
		Transaction transaction2 = new Transaction();
		transaction2.setPaymentType(PaymentType.CREDIT_CARD);
		Item item3 = new Item();
		item3.setName("bike");
		item3.setQuantity("1");
		item3.setPrice("208.00");
		Item item4 = new Item();
		item4.setName("pair of shoes");
		item4.setQuantity("1");
		item4.setPrice("30.00");
		transaction2.setItems(List.of(item3, item4));

		Transaction result2 = this.transactionService.create(transaction2);
		Assertions.assertNotNull(result2);
		Assertions.assertNotNull(result2.getItems());
		Assertions.assertEquals(PaymentStatus.NEW, result2.getPaymentStatus());
		Assertions.assertEquals("238.0", result2.getTotalAmount());

		Transaction result2Step2 = this.transactionService.updatePaymentStatus(result2.getId(), PaymentStatus.CANCELED);
		Assertions.assertNotNull(result2Step2);
		Assertions.assertNotNull(result2Step2.getItems());
		Assertions.assertEquals(PaymentStatus.CANCELED, result2Step2.getPaymentStatus());

		List<Transaction> transactions = this.transactionService.getAll();
		Assertions.assertNotNull(transactions);
		Assertions.assertEquals(2, transactions.size());
	}


}
