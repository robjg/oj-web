package org.oddjob.web.gson.plugin;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.types.ValueFactory;
import org.oddjob.web.gson.GsonConfigurator;

import java.lang.reflect.Array;

/**
 * Bean for an {@link HierarchicalArrayGson} to be used in an oj-gson.xml configuration file.
 */
public class HierarchicalArrayFactoryBean implements ValueFactory<GsonConfigurator>, ArooaSessionAware {

    private String componentClassName;

    private ArooaSession arooaSession;

    @Override
    public void setArooaSession(ArooaSession session) {
        this.arooaSession = session;
    }

    public void setComponentClassName(String componentClassName) {
        this.componentClassName = componentClassName;
    }

    @Override
    public GsonConfigurator toValue() throws ArooaConversionException {

        ClassResolver classResolver = this.arooaSession.getArooaDescriptor().getClassResolver();

        Class<?> componentClass = classResolver.findClass(componentClassName);

        if (componentClass == null) {
            throw new ArooaConversionException(
                    "Failed creating GsonGonfigurator for an Hierarchical Array, no class " + componentClassName);
        }

        Class arrayClass = Array.newInstance(componentClass, 0).getClass();

        return HierarchicalArrayGson.forHierarchicalArray(arrayClass, classResolver);
    }
}
