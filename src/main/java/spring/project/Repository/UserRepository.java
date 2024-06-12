package spring.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.project.Models.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmailId(@Param("email") String email);
   //its a method whose name is findByEmailId and will return the User object
   //When you call this method by providing an email, now that provided email is assigned
   //to String email which is an argument for the function.
   //When you annotate a method parameter with @Param("email"), you are telling Spring Data
   //JPA to bind that method parameter to the named parameter :email in the query.
   //Now, Spring Data JPA automatically generates a query.


}
