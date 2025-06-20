package practiceAPIspring.managingUsers.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


import java.util.Set;
@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE) //?ược cập nhật đồng bộ, chống dirty read (đọc sai).

public class User extends TimeBase{


    @Column(name = "email", unique = true)
    @NotNull(message = "Email không được để trống")
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "number_id", unique = true)
    private String numberId;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    @NotNull(message = "PassWord không được để trống")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private Set<Event> events;

//    @ManyToOne
//    @JoinColumn(name = "manager_id")
//    private User manager;
//
//    @OneToMany(mappedBy = "manager")
//    private Set<User> subordinates;

}
