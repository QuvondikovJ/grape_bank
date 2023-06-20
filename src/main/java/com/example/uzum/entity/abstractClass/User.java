package com.example.uzum.entity.abstractClass;

import com.example.uzum.entity.enums.Gender;
import com.example.uzum.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class User implements UserDetails {
    @Id
    @SequenceGenerator(name = "mySeqGen", sequenceName = "mySeq", initialValue = 2, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "mySeqGen")
    private Integer id;

    @Column
    private String firstname;

    @Column
    private String lastname;

    @Column
    private String middleName;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private Timestamp birthDate;

    @CreationTimestamp
    private Timestamp createdDate;

    @Column
    private Boolean isActive = false;

    @Column
    private String cardNumber;

    @Column
    private String cardExpireDate;


}
