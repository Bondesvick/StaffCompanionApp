package com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening;

import com.stanbic.staffCompanion.CustomerOnboarding.AccountOpening.services.fileGeneration.AOPFileGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@SpringBootApplication
@EnableSwagger2
public class AccountOpeningApplication implements CommandLineRunner {

	@Autowired
	private AOPFileGenerator aopFileGenerator;

	private final Logger log = LoggerFactory.getLogger(AccountOpeningApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AccountOpeningApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		log.info("Testing AOP File Generator");
//
//		aopFileGenerator.generateFile(
//				"AOPTemplate",
//				new AOPContentDTO());
	}
}
