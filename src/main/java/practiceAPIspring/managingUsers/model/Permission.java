package practiceAPIspring.managingUsers.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "permission")
@Getter
@Setter

public class Permission {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "CHAR(36)", updatable = false, nullable = false)
    private String id;

    @Column(name = "role_access", nullable = false)
    private String role;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Role> roles;
}
