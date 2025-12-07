package com.oleksandr.registerms.entity.users;


import com.oleksandr.common.enums.USER_ROLE;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {

    @Id
    private UUID id;

    @Column("username")
    private String username;

    @Column("password")
    private String password;

    @Column("email")
    private String email;

    @Column("role")
    private USER_ROLE role;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("phone_number")
    private String phoneNumber;

    @Column("created_at")
    private LocalDateTime createdAt;

    public User(USER_ROLE role, String username, String password, String email, String firstName, String lastName, String phone, LocalDateTime createdAt) {
        this.role = role;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phone;
        this.createdAt = createdAt;
    }
}
