package br.com.processaarquivo.config;

import br.com.processaarquivo.model.Salesman;
import br.com.processaarquivo.vo.InputData;
import br.com.processaarquivo.vo.InputDataRepository;
import br.com.processaarquivo.vo.OutputData;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.persistence.criteria.CriteriaBuilder;
import javax.sql.DataSource;
import java.util.List;


@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Autowired
    private InputDataRepository inputDataRepository;


    @Value("inputData.csv")
    private Resource inputResources;

    private Resource outputResource = new FileSystemResource("D:/workspace/outputData.csv");

protected List<Salesman> listSalesman;
    @Bean
    public JdbcBatchItemWriter<InputData> writerTOH2(final DataSource dataSource) {

        JdbcBatchItemWriter<InputData> itemWriter = new JdbcBatchItemWriter<InputData>();

        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO INPUT_DATA ( data_type, first_data, second_data, third_data) VALUES ( :dataType, :firstData, :secondData, :thirdData )");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<InputData>());
        return itemWriter;
    }


    @Bean
    public FlatFileItemWriter<OutputData> writer() {
        //Create writer instance
        FlatFileItemWriter<OutputData> writer = new FlatFileItemWriter<>();

        //Set output file location
        writer.setResource(outputResource);

        //All job repetitions should "appendd" to same output file
        writer.setAppendAllowed(true);

        //Name field values sequence based on object properties
        writer.setLineAggregator(new DelimitedLineAggregator<OutputData>() {
            {
                setDelimiter("|");
                setFieldExtractor(new BeanWrapperFieldExtractor<OutputData>() {
                    {
                        setNames(new String[]{"dataType"});
                    }
                });
            }
        });
        return writer;
    }

    @Bean
    public Job readCSVFilesJob(Step step1, Step step2) {
        return jobBuilderFactory
                .get("readCSVFilesJob")
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Step step1(ItemWriter<InputData> writerTOH2) {
        return stepBuilderFactory.get("step1").<InputData, InputData>chunk(5)
                .reader(reader())
                .writer(writerTOH2)
                .build();

    }

    @Bean
    public Step step2(JdbcCursorItemReader jdbcCursorItemReader) {
        return stepBuilderFactory.get("step2").<InputData, InputData>chunk(5)
                .reader(jdbcCursorItemReader)
                .processor(processor(listSalesman))
                .writer(writer())
                .build();

    }

    @Bean
    public JdbcCursorItemReader jdbcCursorItemReader(final DataSource dataSource) {
        JdbcCursorItemReader personJdbcCursorItemReader = new JdbcCursorItemReader<>();
        personJdbcCursorItemReader.setSql("select * from input_data");
        personJdbcCursorItemReader.setDataSource(dataSource);
        personJdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<>(InputData.class));
        return personJdbcCursorItemReader;
    }

    @Bean
    public ItemProcessor<InputData, InputData> processor(List<Salesman> listSalesman) {
        return new SaleProcessor();
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public FlatFileItemReader<InputData> reader() {
        //Create reader instance
        FlatFileItemReader<InputData> reader = new FlatFileItemReader<InputData>();
        reader.setResource(inputResources);
        reader.setLinesToSkip(0);
        //Set number of lines to skips. Use it if file has header rows.

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter("ç");

        tokenizer.setNames(new String[]{"dataType", "firstData", "secondData", "thirdData"});

        DefaultLineMapper lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(

                new BeanWrapperFieldSetMapper<InputData>() {
                    {
                        setTargetType(InputData.class);
                    }
                }
        );
        reader.setLineMapper(lineMapper);
        //Configure how each line will be parsed and mapped to different values
        return reader;
    }
}