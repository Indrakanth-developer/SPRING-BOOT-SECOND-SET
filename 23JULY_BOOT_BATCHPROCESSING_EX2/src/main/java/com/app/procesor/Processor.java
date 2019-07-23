package com.app.procesor;

import org.springframework.batch.item.ItemProcessor;

import com.app.model.Employee;

public class Processor implements ItemProcessor<Employee, Employee> {

	@Override
	public Employee process(Employee item) throws Exception {
		item.setBonus(item.getEsal()*10/100.0);
		return item;
	}

}
