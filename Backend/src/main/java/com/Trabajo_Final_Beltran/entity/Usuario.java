package com.Trabajo_Final_Beltran.entity;

import com.Trabajo_Final_Beltran.enums.Estado;
import com.Trabajo_Final_Beltran.enums.Rol;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "usuario")
public class Usuario implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_establecimiento")
    // @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) para no romper el codigo con archivos de lazy
    private Establecimiento establecimiento;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @JsonIgnore
    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado;
    
    @OneToMany(
        mappedBy = "usuario",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Direccion> direcciones;

    @PrePersist
    public void prePersist() {


        if (this.estado == null) {
            this.estado = Estado.ACTIVO;
        }

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + rol.name()
                )
        );

    }

    @Override
    public String getUsername() {

        return email;

    }

    @Override
    public String getPassword() {

        return password;

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
      return this.estado == Estado.ACTIVO;
    }

}