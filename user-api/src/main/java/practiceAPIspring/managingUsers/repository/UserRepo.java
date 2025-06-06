package practiceAPIspring.managingUsers.repository;

import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import practiceAPIspring.managingUsers.model.User;


import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {
    @Query(value = "SELECT * FROM user WHERE full_name = :name", nativeQuery = true)
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    List<User> findByName(@Param("name") String name);

    @Query(value = "SELECT * FROM user WHERE email = :email", nativeQuery = true)
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Optional<User> findByEmail(@Param("email") String email);

    // dung hql manual query
    // Tìm người dùng theo email
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    @QueryHints(@QueryHint(name = "org.hibernate.cacheable", value = "true"))
    Optional<User> findByEmailManual(String email);


    // Tìm tất cả người dùng có tên chứa chuỗi nào đó (tương tự LIKE '%...%')
    @Query("SELECT u FROM User u WHERE u.fullName LIKE %?1%")
    List<User> findByFullNameContains(String namePart);

    // Tìm tất cả người dùng có vai trò cụ thể (dựa vào tên role)
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = ?1")
    List<User> findByRoleName(String roleName);

    //day cung la inner jion
    // Tìm người dùng có tham gia sự kiện có ID cụ thể
    @Query("SELECT u FROM User u JOIN u.events e WHERE e.id = ?1")
    List<User> findUsersByEventId(Long eventId);

    //inner join by native query
    @Query(
            value = "SELECT u.* FROM user u " +
                    "INNER JOIN user_roles ur ON u.id = ur.user_id " +
                    "INNER JOIN role r ON ur.role_id = r.id " +
                    "WHERE r.name = ?1",
            nativeQuery = true
    )
    List<User> findUsersByRoleName(String roleName);

    // Tìm tất cả user có cùng cả số ID và số điện thoại
    @Query("SELECT u FROM User u WHERE u.numberId = ?1 AND u.phoneNumber = ?2")
    Optional<User> findByNumberIdAndPhoneNumber(String numberId, String phoneNumber);

//ấy toàn bộ user, kể cả user không có role by native query == left join
    @Query(
            value = "SELECT u.* FROM user u " +
                    "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
                    "LEFT JOIN role r ON ur.role_id = r.id",
            nativeQuery = true
    )
    List<User> findAllUsersWithOrWithoutRolesNative();

// lấy toàn bộ user, kể cả user không có role by hql query == left join
    @Query("SELECT u FROM User u LEFT JOIN u.roles r")
    List<User> findAllUsersWithOrWithoutRoles();

    // Tìm tất cả người dùng có số điện thoại bắt đầu bằng một chuỗi cụ thể by native query == right join
    @Query(
            value = "SELECT u.* FROM user u " +
                    "RIGHT JOIN user_roles ur ON u.id = ur.user_id " +
                    "RIGHT JOIN role r ON ur.role_id = r.id",
            nativeQuery = true
    )
    List<User> findRolesWithOrWithoutUsers(); // sẽ trả về user, null nếu không có user

    // Tìm tất cả người dùng và vai trò của họ (bao gồm cả những người không có vai trò) by native query == full outer join
    @Query(
            value = "SELECT DISTINCT u.* FROM user u " +
                    "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
                    "LEFT JOIN role r ON ur.role_id = r.id " +

                    "UNION " +

                    "SELECT DISTINCT u.* FROM user u " +
                    "RIGHT JOIN user_roles ur ON u.id = ur.user_id " +
                    "RIGHT JOIN role r ON ur.role_id = r.id",
            nativeQuery = true
    )
    List<User> findAllUsersAndRoles();

    // Tìm tất cả người dùng và vai trò của họ (bao gồm cả những người không có vai trò) by hql query == cross join
    @Query(
            value = "SELECT u.* FROM user u " +
                    "CROSS JOIN role r",
            nativeQuery = true
    )
    List<User> crossJoinUsersAndRoles(); // Trả về user nhân với mọi role

// Tìm tất cả người dùng có vai trò là "admin" và có số điện thoại bắt đầu bằng "123" == self join
    @Query(
            value = "SELECT u.* FROM user u " +
                    "JOIN user m ON u.manager_id = m.id " +
                    "WHERE m.email = ?1",
            nativeQuery = true
    )
    List<User> findAllSubordinatesOfManager(String managerEmail);


// sort voi jpql
    // Tìm tất cả người dùng và sắp xếp theo tên đầy đủ (tăng dần)
    @Query("SELECT u FROM User u ORDER BY u.fullName ASC")
    List<User> findAllUsersSortedByFullNameAsc();

//   // Tìm tất cả người dùng và sắp xếp theo tên đầy đủ (giảm dần)
    @Query("SELECT u FROM User u ORDER BY u.fullName DESC")
    List<User> findAllUsersSortedByFullNameDesc();

    // sort voi native query
    @Query(value = "SELECT * FROM user ORDER BY full_name ASC", nativeQuery = true)
    List<User> findAllUsersSortedByFullNameAscNative();

    @Query(value = "SELECT * FROM user ORDER BY full_name DESC", nativeQuery = true)
    List<User> findAllUsersSortedByFullNameDescNative();


    @Query(value = "SELECT * FROM user",
            countQuery = "SELECT count(*) FROM user",
            nativeQuery = true)
    Page<User> findAllUsersWithPagingNative(Pageable pageable);


    @Query("SELECT u FROM User u")
    Page<User> findAllUsersWithPagingHQL(Pageable pageable);



    // Tìm tất cả người dùng có vai trò là "ADMIN" và có số điện thoại bắt đầu bằng "098"
    @Query(value = """
    SELECT u.* FROM user u
    JOIN user_roles ur ON u.id = ur.user_id
    JOIN role r ON ur.role_id = r.id
    WHERE r.name = 'ADMIN' AND u.phone_number LIKE '098%'
    """, nativeQuery = true)
    List<User> findAllUsersWithPhoneNumberStartingWith123Native();

    @Query("SELECT u FROM User u WHERE u.phoneNumber LIKE '123%'")
    List<User> findAllUsersWithPhoneNumberStartingWith123HQL();



    //dung slice by paging

    Slice<User> findByFullNameContaining(String fullName, Pageable pageable);

    Slice<User> findAllBy(Pageable pageable);


}
