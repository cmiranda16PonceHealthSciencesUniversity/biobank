package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.proxy.HibernateProxyHelper;

@MappedSuperclass
public abstract class AbstractBiobankModel implements IBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer version;
    private Integer id;

    @Override
    @Id
    @GeneratedValue(generator = "id-generator")
    @GenericGenerator(name = "id-generator",
        strategy = "edu.ualberta.med.biobank.model.type.SmartTableGenerator",
        parameters = {
            @Parameter(name = TableGenerator.CONFIG_PREFER_SEGMENT_PER_ENTITY, value = "true"),
            @Parameter(name = TableGenerator.INCREMENT_PARAM, value = "50")
        })
    @Column(name = "ID", nullable = false)
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Version
    @Column(name = "VERSION", nullable = false)
    public Integer getVersion() {
        return this.version;
    }

    /**
     * DO NOT CALL this method unless, maybe, for tests. Hibernate manages
     * setting this value.
     * 
     * @param version
     */
    @Deprecated
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object that) {
        if (that == this) return true;
        if (that == null) return false;

        // note that HibernateProxyHelper.getClassWithoutInitializingProxy(o)
        // does not seem to work properly in terms of returning the actual
        // class, it may return a superclass, such as, Center. However,
        // Hibernate.getClass() seems to always return the correct instance.
        Class<?> thisClass = Hibernate.getClass(this);
        Class<?> thatClass = Hibernate.getClass(that);
        if (thisClass != thatClass) return false;

        if (that instanceof IBiobankModel) {
            Integer thatId = ((IBiobankModel) that).getId();
            if (getId() != null && getId().equals(thatId)) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (getId() == null) return 0;
        return getId().hashCode();
    }

    @Transient
    public boolean isNew() {
        return getId() == null;
    }

    @SuppressWarnings("unused")
    private static Class<?> proxiedClass(Object o) {
        return HibernateProxyHelper.getClassWithoutInitializingProxy(o);
    }
}
