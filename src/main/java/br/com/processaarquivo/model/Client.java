package br.com.processaarquivo.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Client {
    private String nrCnpj;
    private String name;
    private String businessArea;
}
