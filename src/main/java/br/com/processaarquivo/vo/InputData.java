package br.com.processaarquivo.vo;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class InputData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    Integer id;
    String dataType;
    String firstData;
    String secondData;
    String thirdData;

}
