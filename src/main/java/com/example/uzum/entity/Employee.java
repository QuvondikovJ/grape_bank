package com.example.uzum.entity;

import com.example.uzum.entity.abstractClass.User;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@ApiModel(value = "Employees are saved in this table.")
public class Employee extends User {


    @Column
    private Integer salary;

    @Column
    private Timestamp timeOffWork;

    @ManyToMany
    private Set<Authority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().map(
                        auth-> new SimpleGrantedAuthority(auth.getAuthority()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getIsActive();
    }


}
