package br.com.processaarquivo.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Sale {

    private Long saleId;
    private Item saleItem;
    private String salesmanName;
}
