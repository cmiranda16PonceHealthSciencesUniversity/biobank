package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;

@Entity
@Table(name = "ENTITY_PROPERTY")
public class EntityProperty extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String property;
    private Set<EntityColumn> entityColumnCollection =
        new HashSet<EntityColumn>(0);
    private PropertyType propertyType;
    private Set<EntityFilter> entityFilterCollection =
        new HashSet<EntityFilter>(0);

    @NotEmpty
    @Column(name = "PROPERTY")
    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "ENTITY_PROPERTY_ID", updatable = false)
    public Set<EntityColumn> getEntityColumnCollection() {
        return this.entityColumnCollection;
    }

    public void setEntityColumnCollection(
        Set<EntityColumn> entityColumnCollection) {
        this.entityColumnCollection = entityColumnCollection;
    }

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PROPERTY_TYPE_ID", nullable = false)
    public PropertyType getPropertyType() {
        return this.propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "ENTITY_PROPERTY_ID", updatable = false)
    public Set<EntityFilter> getEntityFilterCollection() {
        return this.entityFilterCollection;
    }

    public void setEntityFilterCollection(
        Set<EntityFilter> entityFilterCollection) {
        this.entityFilterCollection = entityFilterCollection;
    }
}
