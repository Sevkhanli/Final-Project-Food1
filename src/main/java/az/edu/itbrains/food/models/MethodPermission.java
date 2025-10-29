package az.edu.itbrains.food.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "method_permissions")
public class MethodPermission {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "method_permission_role",
            joinColumns = @JoinColumn(name = "method_permissions", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roles", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();

}       