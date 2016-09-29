package org.licket.framework.angular.module.application;

import org.licket.core.LicketApplication;
import org.licket.core.view.hippo.testing.ngclass.AngularClass;
import org.licket.core.view.hippo.testing.ngclass.AngularInjectable;
import org.licket.core.view.hippo.testing.ngmodule.AngularModule;
import org.licket.framework.hippo.PropertyNameBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.licket.framework.hippo.NameBuilder.name;
import static org.licket.framework.hippo.PropertyNameBuilder.property;

/**
 * @author activey
 */
public class ApplicationModule implements AngularModule, AngularClass {

    private final List<AngularInjectable> injectables;
    private final List<AngularClass> classes = newArrayList();

    @Autowired
    private LicketApplication application;

    public ApplicationModule(AngularClass communicationService) {
        this.injectables = asList(communicationService);
        this.classes.add(communicationService);
    }

    @Override
    public PropertyNameBuilder angularName() {
        return property(name("app"), name("AppModule"));
    }

    @Override
    public Iterable<AngularInjectable> injectables() {
        List<AngularInjectable> injectables = newArrayList(this.injectables);
        // TODO make up decision how to define list of application module injectables, should all the licket components be there?
        application.traverseDown(component -> {
            if (!component.getView().isExternalized()) {
                return false;
            }
            injectables.add(component);
            return true;
        });
        return injectables;
    }

    @Override
    public Iterable<AngularClass> classes() {
        List<AngularClass> classes = newArrayList(this.classes);
        // TODO make up decision how to define list of application module classes, should all the licket components be there?
        application.traverseDown(component -> {
            if (!component.getView().isExternalized()) {
                return false;
            }
            classes.add(component);
            return true;
        });
        return classes;
    }
}
