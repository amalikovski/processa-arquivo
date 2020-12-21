package br.com.processaarquivo.config;

import br.com.processaarquivo.domain.DataType;
import br.com.processaarquivo.model.*;
import br.com.processaarquivo.repositories.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class InputDataProcessor implements ItemProcessor<InputData, InputData> {
    @Autowired
    SalesmanRepository salesmanRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    SaleRepository saleRepository;

    @Autowired
    ItemRepository itemRepository;

    public InputData process(InputData inputData) throws Exception {
        if (inputData.getDataType().equals(DataType.SALESMAN.getCode())) {
            Salesman salesman = Salesman.builder()
                    .nrCpf(inputData.getFirstData())
                    .name(inputData.getSecondData())
                    .salary(new BigDecimal(inputData.getThirdData())).build();
            salesmanRepository.save(salesman);
        } else if (inputData.getDataType().equals(DataType.CLIENT.getCode())) {
            Client client = Client.builder()
                    .nrCnpj(inputData.getFirstData())
                    .name(inputData.getSecondData())
                    .businessArea(inputData.getThirdData()).build();
            clientRepository.save(client);
        } else if (inputData.getDataType().equals(DataType.SALE.getCode())) {
            String[] itemsData = inputData.getSecondData().replaceAll("\\[" ,"").replaceAll("\\]" ,"").split(",");
            List<Item> itemList = new ArrayList<>();
            for (String item : itemsData) {
                String[] valuesItem = item.split("-");
                itemList.add(Item.builder().itemId(Long.valueOf(valuesItem[0])).itemQuantity(Integer.valueOf(valuesItem[1])).itemPrice(new BigDecimal(valuesItem[2])).build());
            }
            itemRepository.saveAll(itemList);
            Sale sale = Sale.builder()
                    .saleId(Long.valueOf(inputData.getFirstData()))
                    .saleItem(itemList)
                    .salesmanName(inputData.getThirdData()).build();
            saleRepository.save(sale);
        }

        return inputData;
    }
}