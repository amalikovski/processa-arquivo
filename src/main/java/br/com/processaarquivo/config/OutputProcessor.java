package br.com.processaarquivo.config;

import br.com.processaarquivo.domain.DataType;
import br.com.processaarquivo.model.InputData;
import br.com.processaarquivo.model.OutputData;
import br.com.processaarquivo.model.Salesman;
import br.com.processaarquivo.repositories.SalesmanRepository;
import org.hibernate.result.Output;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;


public class OutputProcessor implements ItemProcessor<List<InputData>, OutputData>
{
    @Autowired
    SalesmanRepository salesmanRepository;

    public OutputData process(List<InputData> inputDataList) throws Exception
    {
        OutputData outputData = OutputData.builder().build();
        inputDataList.stream().forEach(inputData -> {
            if(inputData.getDataType().equals(DataType.SALESMAN.getCode())) {
                System.out.println(inputData.toString());
                Salesman salesman = Salesman.builder()
                        .nrCpf(inputData.getFirstData())
                        .name(inputData.getSecondData())
                        .salary(new BigDecimal(inputData.getThirdData())).build();
                salesmanRepository.save(salesman);
                outputData.setFirstData("aqui");
                System.out.println(salesman.toString());
            }
        });


        return outputData;
    }
}