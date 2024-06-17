package spring.project.wrapper;
import lombok.Data;
import lombok.NoArgsConstructor;

//We dont want to expose password and role in the site.
//So when we have to return the user class, instead of returning the user class
//we will copy the values except password and role into a userwrapper and will return this wrappper.
//also when we take any user inputs, we take them in the form of request map, and then we will copy the values inside the database

@Data
@NoArgsConstructor
public class UserWrapper {
    private  Integer id;
    private String name;
    private String  email;
    private String contactNumber;
    private String status;

    public UserWrapper(Integer id, String name, String email, String contactNumber, String status) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.contactNumber = contactNumber;
        this.status = status;
    }
}