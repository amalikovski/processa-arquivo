package br.com.processaarquivo.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long saleId;

    @ElementCollection(targetClass=Item.class)
    private List<Item> saleItem;

    private String salesmanName;
}
