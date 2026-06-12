package gr.cf9.pants.expense_tracker.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "capabilities")
public class Capability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.PROTECTED)
    @ManyToMany(mappedBy = "capabilities", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    public Set<Role> getAllRoles() {
        return Set.copyOf(roles);
    }

    public void addRole(Role role) {
        roles.add(role);
        role.getCapabilities().add(this);
    }

    public void removeRole(Role role) {
        roles.remove(role);
        role.getCapabilities().remove(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Capability capability)) return false;
        return Objects.equals(getName(), capability.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
