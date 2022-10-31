package com.example.kursovoybot.repository;

import com.example.kursovoybot.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

}
