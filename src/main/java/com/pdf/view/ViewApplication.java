package com.pdf.view;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class ViewApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ViewApplication.class);
		ApplicationContext ac = app.run(args);

		PdfService pdfGenerator = ac.getBean("pdfGenerator", PdfService.class);
		pdfGenerator.generatePdf();
	}

}
