package com.app.config;

import javax.sql.DataSource;

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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.app.model.Employee;
import com.app.procesor.Processor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

	//item readers processors writers
	@Bean
	public ItemReader<Employee> reader(){
		FlatFileItemReader<Employee> ir=new FlatFileItemReader<>();
		ir.setResource(new ClassPathResource("emp.csv"));
		ir.setLineMapper(
				new DefaultLineMapper<Employee>() {{

					setLineTokenizer(new DelimitedLineTokenizer() {{

						setNames("eid","ename","esal");
					}}
							);

					setFieldSetMapper(new BeanWrapperFieldSetMapper<Employee>() {{
						
						setTargetType(Employee.class);
					}}
							
							
							);
				}});

		return ir;
	}



	@Bean
	public ItemProcessor<Employee, Employee> processor(){
		return (p)->{p.setBonus(p.getEsal()*10/100.0);
						return p;
						};
//		return new Processor();
	}


	@Bean
	public ItemWriter<Employee> writer(){
		JdbcBatchItemWriter<Employee> jw=new JdbcBatchItemWriter<>();
		jw.setDataSource(dataSource());
		jw.setItemSqlParameterSourceProvider(
				new BeanPropertyItemSqlParameterSourceProvider<Employee>());
		jw.setSql("INSERT INTO EMPLOYEE (EID,ENAME,ESAL,BONUS) VALUES (:eid,:ename,:esal,:bonus)");
		
		return jw;
	}



	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource ds=new DriverManagerDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost:3306/test");
		ds.setUsername("root");
		ds.setPassword("root");
		return ds;
	}




	//step
	@Autowired
	private StepBuilderFactory sf;
	
	@Bean
	public Step step1() {
		return sf.get("step1").<Employee,Employee>chunk(5)
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}

	@Autowired
	private JobBuilderFactory jf;
	
	@Bean
	public Job job1() {
	
		return jf.get("job1").incrementer(new RunIdIncrementer()).start(step1()).build();
	}
}
