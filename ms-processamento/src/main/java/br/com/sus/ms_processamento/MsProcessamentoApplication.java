package br.com.sus.ms_processamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MsProcessamentoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsProcessamentoApplication.class, args);
	}

}
