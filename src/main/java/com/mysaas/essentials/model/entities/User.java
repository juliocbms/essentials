package com.mysaas.essentials.model.entities;



import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;

    @Column(name = "name",nullable = false)
    private String Name;

    @Column(name = "email",nullable = false)
    private String Email;

    @Column(name = "username",nullable = false)
    private String Username;

    @Column(name = "password",nullable = false)
    private String PasswordHash;

    @Column(name = "status",nullable = false)
    private boolean Active;

    @Column(name = "createdAt",nullable = false)
    @CreatedDate
    private LocalDateTime CreatedAt;

    @Column(name = "updatedAt",nullable = true)
    @LastModifiedDate
    private LocalDateTime UpdatedAt;

    @Column(name = "lastloginAt",nullable = true)
    private LocalDateTime LastLoginAt;

    @Column(name = "emailVerified",nullable = false)
    private boolean EmailVerified;

    @Column(name = "deletedAt",nullable = true)
    private LocalDateTime DeletedAt;


    public User() {
    }

    public User(UUID id, String name, String email, String username, String passwordHash, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLoginAt, boolean emailVerified, LocalDateTime deletedAt) {
        Id = id;
        Name = name;
        Email = email;
        Username = username;
        PasswordHash = passwordHash;
        Active = active;
        CreatedAt = createdAt;
        UpdatedAt = updatedAt;
        LastLoginAt = lastLoginAt;
        EmailVerified = emailVerified;
        DeletedAt = deletedAt;
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return PasswordHash;
    }

    public String getUsername() {
        return Username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPasswordHash() {
        return PasswordHash;
    }

    public void setPasswordHash(String passwordHash) {
        PasswordHash = passwordHash;
    }

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
    }

    public LocalDateTime getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        CreatedAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return UpdatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        UpdatedAt = updatedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return LastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        LastLoginAt = lastLoginAt;
    }

    public boolean isEmailVerified() {
        return EmailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        EmailVerified = emailVerified;
    }

    public LocalDateTime getDeletedAt() {
        return DeletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        DeletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "Id='" + Id + '\'' +
                ", Name='" + Name + '\'' +
                ", Email='" + Email + '\'' +
                ", Username='" + Username + '\'' +
                ", PasswordHash='" + PasswordHash + '\'' +
                ", Active=" + Active +
                ", CreatedAt=" + CreatedAt +
                ", UpdatedAt=" + UpdatedAt +
                ", LastLoginAt=" + LastLoginAt +
                ", EmailVerified=" + EmailVerified +
                ", DeletedAt=" + DeletedAt +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(Id, user.Id) && Objects.equals(Name, user.Name) && Objects.equals(Email, user.Email) && Objects.equals(Username, user.Username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, Name, Email, Username);
    }
}
