/*
package ir.aja.matna.servicebatchprocess.configurationbatch;

import ir.aja.matna.servicebatchprocess.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableBatchProcessing
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ItemReader<Transaction> itemReader;
    private final ItemWriter<Transaction> itemWriter;
    private final ItemProcessor<Transaction,Transaction> itemProcessor;


    @Bean
    public Step stepTransaction() {
        return stepBuilderFactory.get("stepTransaction")
                .<Transaction, Transaction>chunk(100)
                .reader(itemReader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean("jobTransaction")
    public Job jobTransaction() {
        return jobBuilderFactory.get("jobTransaction")
                .start(stepTransaction()).build();
    }
}
*/
