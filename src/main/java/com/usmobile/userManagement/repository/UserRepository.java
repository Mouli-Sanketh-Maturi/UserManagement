package com.usmobile.userManagement.repository;

import com.usmobile.userManagement.entity.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Save User to database
     *
     * @param user User to save
     * @return User saved instance of the user
     */
    User save(User user);

    /**
     * Find whether a user exists by email
     *
     * @param email
     * @return true if user exists, false otherwise
     */
    Boolean existsByEmail(String email);

}
