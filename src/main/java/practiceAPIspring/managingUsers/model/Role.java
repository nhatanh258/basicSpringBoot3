package practiceAPIspring.managingUsers.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role")
@Getter
@Setter
@Builder
public class Role {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "name")
    private String name;


    @Column(name = "description")
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions", // tên bảng trung gian
            joinColumns = @JoinColumn(name = "permission_id"), // FK trỏ tới Role
            inverseJoinColumns = @JoinColumn(name = "role_id") // FK trỏ tới User
    )
    private Set<Permission> permissions;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<User> users;
    @Override
    public String toString() {
        return "Role(id=" + id + ", name=" + name + ", description=" + description + ")";
    }
}
