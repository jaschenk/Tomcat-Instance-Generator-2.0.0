package jeffaschenk.infra.tomcat.instance.generator.model;


import lombok.Data;

/**
 * InstanceJVMOption
 *
 * Created by schenkje on 2/18/2017.
 */
@Data
public class InstanceJVMOption {

    public String jvmOption;

    /**
     * Default Constructors
     */
    public InstanceJVMOption() {
    }

    public InstanceJVMOption(String jvmOption) {
        this.jvmOption = jvmOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstanceJVMOption)) return false;

        InstanceJVMOption that = (InstanceJVMOption) o;

        return getJvmOption() != null ? getJvmOption().equals(that.getJvmOption()) : that.getJvmOption() == null;
    }

    @Override
    public int hashCode() {
        return getJvmOption() != null ? getJvmOption().hashCode() : 0;
    }
}
