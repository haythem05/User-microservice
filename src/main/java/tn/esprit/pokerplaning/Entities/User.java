package tn.esprit.pokerplaning.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
//import tn.esprit.pokerplaning.Entities.ProjectTeam.Affectation;
//import tn.esprit.pokerplaning.Entities.Room.Room;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@Entity
@Data
@Table(name ="`user`")
@Builder
@AllArgsConstructor
@NoArgsConstructor



public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;



    private String firstName;
    private String lastName;

    private String email;

    private String password;


    private String resetToken;
    private boolean banned;
    private LocalDateTime dateToken;
    private  String phone;
    private int loginAttempts;



    @Lob()
    @Column(columnDefinition = "MEDIUMBLOB")
    byte[] image;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Role role;

    private int skillRate;

  // @ManyToMany
  //  private List<Room> rooms;

  //  @OneToMany(mappedBy = "userAffected")
   // private List<Affectation> affectations;

    @Override

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority((role.name())));
    }

    @Override
    public String getUsername() {
        return email;
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
        return true ;
    }

    @Override
    public boolean isEnabled() {
        return true ;
    }
}

