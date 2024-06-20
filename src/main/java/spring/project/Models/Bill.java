package spring.project.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name = "Bill.getAllBills" , query = "select b from bill b order by b.id desc")
@NamedQuery(name = "Bill.getBillByUserName" , query = "select b from bill b where b.createdBy=:username order by b.id desc")


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@DynamicUpdate
@DynamicInsert
@Entity(name = "bill")
public class Bill implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "contactnumber")
    private String contactNumber;

    @Column(name = "paymentmethod")
    private String paymentMethod;

    @Column(name = "total")
    private Integer total;

    @Column(name = "productdetails" , columnDefinition = "json")
    private String productDetails;
//  Specifies that the column should be created with the JSON data type.

    @Column(name = "createdby")
    private String createdBy;

}