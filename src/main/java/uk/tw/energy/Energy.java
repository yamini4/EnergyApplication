package uk.tw.energy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.tw.energy.domain.Employee;

@SpringBootApplication
public class Energy {
	public static void main(String[] args) {

		List<Employee> employees = new ArrayList<>();
		employees.add(new Employee("Jane", "Valley"));

		employees.add(new Employee("Mike", "Vanderbilt"));

		employees.add(new Employee("Emily", "Smith"));

		List<Employee> updatedEmployees = employees.stream()
				.map(empl -> new Employee(empl.getFirstName().toUpperCase(), empl.getLastName().toUpperCase()))
				.filter(emp -> emp.getLastName().startsWith("V")).collect(Collectors.toList());

		updatedEmployees.stream().forEach(System.out::println);

		SpringApplication.run(Energy.class, args);
	}
}
