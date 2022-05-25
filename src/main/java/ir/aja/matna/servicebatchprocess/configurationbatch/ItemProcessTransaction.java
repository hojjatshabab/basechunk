package ir.aja.matna.servicebatchprocess.configurationbatch;

import ir.aja.matna.servicebatchprocess.model.Transaction;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemProcessTransaction implements ItemProcessor<Transaction,Transaction>{
    @Override
    public Transaction process(Transaction transaction) throws Exception {

        System.out.println("processing transaction " + transaction);
        return transaction;
    }
}
