package br.com.processaarquivo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long itemId;
    private Integer itemQuantity;
    private BigDecimal itemPrice;

    @ManyToOne
    @JoinColumn(name = "sale_Id")
    private Sale sale;

}
