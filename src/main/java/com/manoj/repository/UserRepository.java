package com.manoj.repository;

import com.manoj.models.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Manoj Baral on 9/24/2017.
 */
public interface UserRepository extends CrudRepository<User, Long> {
}
