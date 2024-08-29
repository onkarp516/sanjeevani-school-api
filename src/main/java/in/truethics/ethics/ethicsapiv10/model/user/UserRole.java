package in.truethics.ethics.ethicsapiv10.model.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import in.truethics.ethics.ethicsapiv10.model.access_permissions.SystemAccessPermissions;
import in.truethics.ethics.ethicsapiv10.model.master.Branch;
import in.truethics.ethics.ethicsapiv10.model.master.Outlet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role_tbl")
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String roleName;
    @Column(columnDefinition = "bit(1) default 1", nullable = false)
    private Boolean status;
    private Long createdBy;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private Long updatedBy;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL})
    @JsonIgnoreProperties(value = {"role_tbl", "hibernateLazyInitializer"})
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.MERGE})
    @JsonIgnoreProperties(value = {"role_tbl", "hibernateLazyInitializer"})
    @JoinColumn(name = "outlet_id")
    private Outlet outlet;

    @OneToMany(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<SystemAccessPermissions> systemAccessPermissions;

    @JsonBackReference
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Users> users;

}
