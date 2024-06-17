package spring.project.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.project.Models.User;
import spring.project.wrapper.UserWrapper;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmailId(@Param("email") String email);
   //its a method whose name is findByEmailId and will return the User object
   //When you call this method by providing an email, now that provided email is assigned
   //to String email which is an argument for the function.
   //When you annotate a method parameter with @Param("email"), you are telling Spring Data
   //JPA to bind that method parameter to the named parameter :email in the query.
   //Now, Spring Data JPA automatically generates a query.

    List<UserWrapper> getAllUser();

    @Transactional
    @Modifying     //as we are modifying something inside the database we should write these annotations.
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id);

    List<String> getAllAdmin_mails();

    User findByEmail(String email);


}
