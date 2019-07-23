package com.app.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.app.processor.Processor;
import com.app.reader.Reader;
import com.app.writer.Writer;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	
	//1. beans for readers writers and processors
	@Bean
	public Reader reader() {
		return new Reader();
	}
	@Bean
	public Writer writer() {
		return new Writer();
	}
	@Bean
	public Processor processor() {
		return new Processor();
		
	}
	
	@Autowired
	private StepBuilderFactory sf;
	
	@Bean
	public Step step1() {
		return sf.get("step1").<String,String>chunk(4).reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}
	
	@Autowired
	private JobBuilderFactory jf;
	
	@Bean
	public Job job1() {
		return jf.get("job1")/*.incrementer(new RunIdIncrementer())*/
				.start(step1())
				.build();
	}
}
