package br.com.processaarquivo.config;

import br.com.processaarquivo.model.InputData;
import br.com.processaarquivo.repositories.InputDataRepository;
import br.com.processaarquivo.model.OutputData;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

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


    private Resource inputResource = new FileSystemResource("data/in/inputData.csv");

    private Resource outputResource = new FileSystemResource("data/out/outputData.csv");



    @Bean
    public Job analyseDataJob(Step step1, Step step2) {
        return jobBuilderFactory
                .get("analyseDataJob")
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .next(step2)
                .build();
    }

    @Bean
    public Step step1(ItemWriter<InputData> writerToDatabase) {
        return stepBuilderFactory.get("step1").<InputData, InputData>chunk(5)
                .reader(reader())
                .writer(writerToDatabase)
                .build();

    }

    @Bean
    public Step step2(JdbcCursorItemReader jdbcCursorItemReader) {
        return stepBuilderFactory.get("step2").<InputData, InputData>chunk(5)
                .reader(jdbcCursorItemReader)
                .processor(processor())
                .writer(writerToFile())
                .build();

    }

    @Bean
    public JdbcCursorItemReader itemReaderFinalOutputData(final DataSource dataSource) {
        JdbcCursorItemReader personJdbcCursorItemReader = new JdbcCursorItemReader<>();
        personJdbcCursorItemReader.setSql("select * from input_data");
        personJdbcCursorItemReader.setDataSource(dataSource);
        personJdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<>(InputData.class));
        return personJdbcCursorItemReader;
    }

    @Bean
    public ItemProcessor<InputData, InputData> processor() {
        return new InputDataProcessor();
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    @Bean
    public FlatFileItemReader<InputData> reader() {
        FlatFileItemReader<InputData> reader = new FlatFileItemReader<InputData>();
        reader.setResource(inputResource);
        reader.setLinesToSkip(0);

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
        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<InputData> writerToDatabase(final DataSource dataSource) {
        JdbcBatchItemWriter<InputData> itemWriter = new JdbcBatchItemWriter<InputData>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO INPUT_DATA ( data_type, first_data, second_data, third_data) VALUES ( :dataType, :firstData, :secondData, :thirdData )");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<InputData>());
        return itemWriter;
    }


    @Bean
    public FlatFileItemWriter<OutputData> writerToFile() {
        FlatFileItemWriter<OutputData> writer = new FlatFileItemWriter<>();
        writer.setResource(outputResource);
        writer.setAppendAllowed(true);
        writer.setLineAggregator(new DelimitedLineAggregator<OutputData>() {
            {
                setDelimiter("|");
                setFieldExtractor(new BeanWrapperFieldExtractor<OutputData>() {
                    {
                        setNames(new String[]{"firstData"});
                    }
                });
            }
        });
        return writer;
    }
}