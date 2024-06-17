package spring.project.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

//import javax.persistence.Entity;
import java.io.Serializable;

//The @DynamicUpdate annotation is used to optimize update operations.
//When this annotation is applied to an entity, Hibernate will generate SQL
//UPDATE statements that include only the fields that have changed.
//By default, Hibernate includes all fields in the UPDATE statement, even
//if only a subset of the fields has been modified. This can lead to unnecessary
//updates and potentially more contention in the database.
//Without @DynamicUpdate:(all fields in the table will be updated)
//UPDATE User SET name = ?, email = ? WHERE id = ?
//With @DynamicUpdate, if only the email is changed, the update might look like this
//UPDATE User SET email = ? WHERE id = ?

//The @DynamicInsert annotation is used to optimize insert operations.
//When this annotation is applied to an entity, Hibernate will generate
//SQL INSERT statements that include only the non-null fields. By default,
//Hibernate includes all fields in the INSERT statement, setting null values
//explicitly for fields that are not set.
//With @DynamicInsert, if the email is null, the insert might look like this:
//INSERT INTO User (id, name) VALUES (?, ?)

//the Serializable interface is used to enable serialization and deserialization
//of an object. Serialization is the process of converting an object into a byte
//stream, which can then be saved to a file, sent over a network, or stored in a
//database. Deserialization is the reverse process, where the byte stream is
//converted back into a copy of the object.
//When a class implements the Serializable interface, it indicates that its objects can
//be serialized and deserialized. The Serializable interface is a marker interface, meaning
//it doesn't contain any methods; it simply marks a class as being capable of serialization.

//The serialVersionUID is like a unique ID for a class that can be saved and loaded.
//When you save an object to a file (serialization) and later load it back (deserialization),
//this ID helps make sure the object's data can be correctly restored or not. It throws an
//error if it cant able to restore
//Implementing Serializable can be beneficial if your use case changes to include network
//transmission session storage, or distributed caching.

@NamedQuery(name = "User.findByEmailId", query = "select u from User u where u.email=:email")
//The User. is used to denote that the named query is associated with the User class.
//findByEmailId is the name of the named query.
//Make sure that you use class conventions in java code but not in actual database.


@NamedQuery(name = "User.getAllUser", query = "select " +
                "new spring.project.wrapper.UserWrapper(u.id , u.name , u.email , u.contactNumber , u.status) " +
                "from User u where u.role = 'user'")
//retreiving the user values from the database and send them as parameteres to the UserWrapper constructor and
// returning the UserWrapper object.
//make sure you are using attribute names in u.id, u.email, etc... must be of java code.


@NamedQuery(name = "User.updateStatus", query = "update User u set u.status=:status where u.id =:id")

@NamedQuery(name = "User.getAllAdmin_mails", query = "select u.email from User u where u.role = 'admin'")


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@DynamicInsert  //upto here, above all comes under lombok library
@Entity   //to connnect this table with sql database
@Table(name = "user")    //name of connected table in SQL database is 'user'
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "contactNumber")
    private String contactNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "role")
    private String role;

}