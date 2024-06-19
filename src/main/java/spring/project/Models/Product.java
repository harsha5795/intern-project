package spring.project.Models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@NamedQuery(name = "Product.getAllProduct", query = "select new spring.project.wrapper.ProductWrapper(u.id , u.name , u.description , u.price , u.category.id , u.category.name , u.status) from Product u")

@NamedQuery(name = "Product.updateProductStatus" , query = "update Product u set u.status =:status where u.id =:id")

@NamedQuery(name = "Product.getByCategory", query = "select new spring.project.wrapper.ProductWrapper(u.id , u.name) from Product u where u.category.id=:id and u.status='true'")

@NamedQuery(name = "Product.getProductById", query = "select new spring.project.wrapper.ProductWrapper(u.id , u.name , u.description , u.price) from Product u where u.id=:id")


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@DynamicUpdate
@DynamicInsert
@Entity
@Table(name = "product")
public class Product implements Serializable {
    private static final long serialVersionUID = 123456L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_fk", nullable = false)
    private Category category;
    //many products point to a category so ManyToOne
    //category will be loaded lazily. So when you load a product, the Category data is not fetched from the
    //database until you explicitly access it.
    //EAGER: product and Category are both fetched from the database immediately.
    //LAZY: Only product is fetched initially. The Category is fetched only when you call product.getCategory().
    //nullable=false, means each product should definitely be a part of category no null values allowed.


    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    private String status;

}