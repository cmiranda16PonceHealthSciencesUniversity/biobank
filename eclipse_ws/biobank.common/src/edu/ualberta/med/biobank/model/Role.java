package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreInsert;
import edu.ualberta.med.biobank.validator.group.PreUpdate;

@Entity
@Table(name = "ROLE")
@Unique.List({
    @Unique(properties = { "name" },
        groups = { PreInsert.class, PreUpdate.class },
        message = "{edu.ualberta.med.biobank.model.Role.name.Unique}")
})
@NotUsed(by = Membership.class, property = "roleCollection",
    message = "edu.ualberta.med.biobank.model.Role.NotUsed.membership.roleCollection")
public class Role extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String name;
    private Set<PermissionEnum> permissionCollection =
        new HashSet<PermissionEnum>(0);

    @NotEmpty(message = "edu.ualberta.med.biobank.model.Role.name.NotEmpty")
    @Column(name = "NAME", unique = true, nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ElementCollection(targetClass = PermissionEnum.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "ROLE_PERMISSION",
        joinColumns = @JoinColumn(name = "ID"))
    @Column(name = "PERMISSION_NAME")
    @Enumerated(EnumType.STRING)
    public Set<PermissionEnum> getPermissionCollection() {
        return this.permissionCollection;
    }

    public void setPermissionCollection(
        Set<PermissionEnum> permissionCollection) {
        this.permissionCollection = permissionCollection;
    }
}
