package com.example.uzum.entity;

import com.example.uzum.entity.abstractClass.User;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ApiModel(value = "Buyers are saved in this table.")
public class Buyer extends User {

    @Column
    private Integer balance = 0; // if buyer fills his balance before buying anything and if filled sum greater than 1 mln, then we will give him 0.5% cashback.

    @Column
    private Double cashbackPercent = 0.0; // We offer to our buyers cashback percent, so when user in full month buys 10 products and his cost of purchased products more than 10 mln Uzs
    // he will have 0.5% cashback in his every bought product, 20 and 20 mln Uzs -> 1% cashback, 30 and 30 mln 0> 1.5% cashback by the end of this month,
    // from the beginning new month again cashback will be cleaned, but balance won't be changed

    @Column
    private String referralLink = "https://grape-bank.uz/user/referal/"; // for every registered buyer via this link is paid 3000 UZS

    @OneToOne
    private Branch branch;


    @ManyToMany
    private Set<Authority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream().map(
                        auth -> new SimpleGrantedAuthority(auth.getAuthority()))
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
