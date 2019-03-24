package jeffaschenk.infra.tomcat.instance.generator.model;


import lombok.Data;

/**
 * InstanceProperty
 *
 * Created by schenkje on 2/18/2017.
 */
@Data
public class InstanceProperty {

    public String propertyName;
    public String propertyValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstanceProperty)) return false;

        InstanceProperty that = (InstanceProperty) o;

        return getPropertyName() != null ? getPropertyName().equals(that.getPropertyName()) : that.getPropertyName() == null;
    }

    @Override
    public int hashCode() {
        return getPropertyName() != null ? getPropertyName().hashCode() : 0;
    }
}
