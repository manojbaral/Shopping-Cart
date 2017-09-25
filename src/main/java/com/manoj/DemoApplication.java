package com.manoj;

import com.manoj.models.Product;
import com.manoj.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner{

    @Autowired
    private ProductRepository productRepository;


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

    }
        @Override
        public void run(String... strings) throws Exception {
            Product product1 = new Product();
            product1.setName("macbook");
            product1.setDescription("Just when you thought your MacBook Pro was state of the art, Apple introduces the MF839LL/A 13\" MacBook Pro with new advanced processing power and graphics.");
            product1.setPrice(1129.23);

            Product product2 = new Product();
            product2.setName("iphone8");
            product2.setDescription("Phone 8 dramatically improves the most important aspects of the iPhone experience. It introduces advanced new camera systems. The best performance and battery life ever in an iPhone. ");
            product2.setPrice(978.14);

            if (productRepository.findByName("macbook")==null) {
                productRepository.save(product1);
            }
            if (productRepository.findByName("iphone8")==null) {
                productRepository.save(product2);
            }
	}
}
