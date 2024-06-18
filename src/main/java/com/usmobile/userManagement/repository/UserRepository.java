package com.usmobile.userManagement.repository;

import com.usmobile.userManagement.entity.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    public User save(User user);

    public Boolean existsByEmail(String email);

}
