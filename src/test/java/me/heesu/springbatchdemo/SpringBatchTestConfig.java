package me.heesu.springbatchdemo;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
public class SpringBatchTestConfig {

    @Test
    void test() {
        List<Integer> intlist = new ArrayList<>();
        intlist.add(1);
        intlist.add(2);

        List<Double> dlist = new ArrayList<>();
        dlist.add(1.123);
        dlist.add(2.345);

        process(intlist);
        process(dlist);
    }

    public void process(List<? extends Number> numbers) {
        System.out.println(numbers.stream()
                .mapToDouble(Number::doubleValue)
                .reduce(0, Double::sum));
    }
}
