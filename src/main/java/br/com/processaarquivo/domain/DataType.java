package br.com.processaarquivo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DataType {
    SALESMAN("001", "SALESMAN"),
    CLIENT("002", "CLIENT"),
    SALE("003", "SALE");

    private String code;
    private String name;

    public static DataType getByCode(String code) {
        return Arrays.asList(values()).stream().filter(v ->
                v.getCode().equals(code)).findFirst().orElse( null);
    }
}
