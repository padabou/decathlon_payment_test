package com.decathlon.payment.payment;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.springframework.context.annotation.Lazy;

import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "transaction")
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_gen")
    @GenericGenerator(name = "sequence_gen", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "sequence"),
            @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.VALUE_COLUMN_PARAM, value = "next_val"),
            @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.INITIAL_PARAM, value = "100000"),
            @org.hibernate.annotations.Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @Column(nullable = false)
    private int id;
    private String totalAmount;
    private PaymentType paymentType;
    private PaymentStatus paymentStatus;
    @OneToMany(mappedBy = "transaction", fetch = FetchType.EAGER) // eager is not a good practise, could be done in repository or in a second request
    private List<Item> items;
}
