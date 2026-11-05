package com.marriage;
import jakarta.persistence.*;
@Entity @Table(name="gifts")
public class Gift {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public String name;
 public String description;
 public String category;
 @Column(precision=12, scale=2) public java.math.BigDecimal price;
 @Column(name="image_url", length=500) public String imageUrl;
 @Column(name="product_url", length=500) public String productUrl;
 public boolean reserved;
 @Column(name="guest_name") String guestName;
 String message;
}
