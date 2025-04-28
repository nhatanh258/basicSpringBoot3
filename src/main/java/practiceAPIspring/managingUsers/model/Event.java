package practiceAPIspring.managingUsers.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.Set;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event")
@Getter
@Setter
public class Event {

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
        @JsonIgnore
        @JoinTable(
                name = "user_events", // tên bảng trung gian
                joinColumns = @JoinColumn(name = "event_id"), // FK trỏ tới Role
                inverseJoinColumns = @JoinColumn(name = "user_id") // FK trỏ tới User
        )
        private Set<User> users;
}
