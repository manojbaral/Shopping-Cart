package com.manoj.repository;

import com.manoj.models.Product;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Manoj Baral on 9/24/2017.
 */
public interface ProductRepository extends CrudRepository<Product, Long> {
    Product findByName(String name);
}
