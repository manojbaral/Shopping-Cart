package com.manoj.repository;

import com.manoj.models.CartItem;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Manoj Baral on 9/24/2017.
 */
public interface CartItemRepository extends CrudRepository<CartItem, Long> {
}
