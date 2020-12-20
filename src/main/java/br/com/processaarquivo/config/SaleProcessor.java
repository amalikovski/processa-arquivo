package br.com.processaarquivo.config;

import br.com.processaarquivo.domain.DataType;
import br.com.processaarquivo.model.Salesman;
import br.com.processaarquivo.vo.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;


public class SaleProcessor implements ItemProcessor<InputData, InputData>
{
    @Autowired
    SalesmanRepository salesmanRepository;

    public InputData process(InputData inputData) throws Exception
    {

        if(inputData.getDataType().equals(DataType.SALESMAN.getCode())) {
            System.out.println(inputData.toString());
            Salesman salesman = Salesman.builder()
                    .nrCpf(inputData.getFirstData())
                    .name(inputData.getSecondData())
                    .salary(new BigDecimal(inputData.getThirdData())).build();
            salesmanRepository.save(salesman);

            System.out.println(salesman.toString());
        }

        return inputData;
    }
}