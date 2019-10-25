package com.sfarias.user.model;

import com.sfarias.user.utils.validations.CheckEmail;
import com.sfarias.user.utils.validations.CheckPassword;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(255)")
    private UUID id;
    private String name;
    @CheckEmail
    @NotNull
    @NotEmpty
    private String email;
    @CheckPassword
    private String password;
    private UUID token;
    private Date created;
    private Date updated;
    private Date lastLogin;
    private Boolean isActive;
    @OneToMany(mappedBy = "user")
    private List<Phone> phones;
}
