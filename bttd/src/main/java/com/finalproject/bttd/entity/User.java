package com.finalproject.bttd.entity;
import lombok.*;
import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Entity
@Table(name="user")

public class User implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "user_id")
    private String user_id;
    @Column(name="user_name")
    private String user_name;
    @Column(name="user_age")
    private String user_age;
    @Column(name="user_weight")
    private String user_weight;
    @Column(name="user_score")
    private String user_score;
    @Column(name="user_password")
    private String user_password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    , inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();



}
