package com.example.seurity.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "resetpwd")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String resetpwdId;
    private String token;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @CreationTimestamp
    @Column(updatable = true)
    private Date createdAt;
    @UpdateTimestamp
    private Date updatedAt;
    private LocalDateTime expiryDate;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String resetpwdId, String token, User user, Date createdAt, Date updatedAt, LocalDateTime expiryDate) {
        this.resetpwdId = resetpwdId;
        this.token = token;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiryDate = expiryDate;
    }

    public String getResetpwdId() {
        return resetpwdId;
    }

    public void setResetpwdId(String resetpwdId) {
        this.resetpwdId = resetpwdId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}



