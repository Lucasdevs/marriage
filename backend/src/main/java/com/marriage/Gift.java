package com.marriage;
import jakarta.persistence.*;
@Entity @Table(name="gifts")
public class Gift {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public String name;
 public String description;
 public String category;
 public boolean reserved;
 @Column(name="guest_name") String guestName;
 String message;
}
