package ir.aja.matna.servicebatchprocess.configurationbatch;

import ir.aja.matna.servicebatchprocess.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.integration.chunk.RemoteChunkingMasterStepBuilderFactory;
import org.springframework.batch.integration.chunk.RemoteChunkingWorkerBuilder;
import org.springframework.batch.integration.config.annotation.EnableBatchIntegration;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.amqp.dsl.Amqp;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

@Configuration
@EnableBatchProcessing
@EnableBatchIntegration
public class BatchRemoteChunkingConfiguration {

    @Profile("!worker")
    @Configuration
    @RequiredArgsConstructor
    public static class MasterConfiguration {

        private final JobBuilderFactory jobBuilderFactory;
        private final RemoteChunkingMasterStepBuilderFactory remoteChunkingMaster;
        private final ItemReader<Transaction> itemReader;

        @Bean
        public DirectChannel requests() {
            return new DirectChannel();
        }

        @Bean
        public IntegrationFlow outboundFlow(AmqpTemplate amqpTemplate) {
            return IntegrationFlows
                    .from(requests())
                    .handle(Amqp.outboundAdapter(amqpTemplate)
                            .routingKey("requests"))
                    .get();
        }

        @Bean
        public QueueChannel replies() {
            return new QueueChannel();
        }

        @Bean
        public IntegrationFlow inboundFlow(ConnectionFactory connectionFactory) {
            return IntegrationFlows
                    .from(Amqp.inboundAdapter(connectionFactory, "replies"))
                    .channel(replies())
                    .get();
        }

        @Bean
        public TaskletStep masterStep() {
            return this.remoteChunkingMaster.get("masterStep")
                    .<Transaction, Transaction>chunk(100)
                    .reader(itemReader)
                    .outputChannel(requests())
                    .inputChannel(replies())
                    .build();
        }

        @Bean
        public Job remoteChunkingJob() {
            return this.jobBuilderFactory.get("remoteChunkingJob")
                    .start(masterStep())
                    .build();
        }


    }

    @Profile("worker")
    @Configuration
    @RequiredArgsConstructor
    public static class Worker {

        private final RemoteChunkingWorkerBuilder<Transaction, Transaction> workerBuilder;
        private final ItemWriter<Transaction> itemWriter;
        private final ItemProcessor<Transaction, Transaction> itemProcessor;


        @Bean
        public DirectChannel requests() {
            return new DirectChannel();
        }

        @Bean
        public DirectChannel replies() {
            return new DirectChannel();
        }

        @Bean
        public IntegrationFlow outboundFlow(AmqpTemplate amqpTemplate) {
            return IntegrationFlows
                    .from(replies())
                    .handle(Amqp.outboundAdapter(amqpTemplate)
                            .routingKey("replies"))
                    .get();
        }

        @Bean
        public IntegrationFlow inboundFlow(ConnectionFactory connectionFactory) {
            return IntegrationFlows
                    .from(Amqp.inboundAdapter(connectionFactory, "requests"))
                    .channel(requests())
                    .get();
        }

        @Bean
        public IntegrationFlow integrationFlow() {
            return this.workerBuilder
                    .itemProcessor(itemProcessor)
                    .itemWriter(itemWriter)
                    .inputChannel(requests())
                    .outputChannel(replies())
                    .build();
        }

    }
}
