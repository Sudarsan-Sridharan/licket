package org.licket.core.view.list;

import static java.lang.String.format;
import static org.licket.core.view.LicketComponentView.noView;
import java.util.Optional;
import org.licket.core.model.LicketComponentModel;
import org.licket.core.module.application.LicketComponentModelReloader;
import org.licket.core.view.LicketComponent;
import org.licket.core.view.container.AbstractLicketMultiContainer;
import org.licket.core.view.render.ComponentRenderingContext;

public abstract class AbstractLicketList<T> extends AbstractLicketMultiContainer<String> {

    private Class<T> elementClass;

    public AbstractLicketList(String id, LicketComponentModel<String> enclosingComponentPropertyModel,
                              Class<T> elementClass, LicketComponentModelReloader modelReloader) {
        super(id, String.class, enclosingComponentPropertyModel, noView(), modelReloader);
        this.elementClass = elementClass;
        // TODO analyze element class provided and check its properties against passed enclosingComponentPropertyModel
    }

    @Override
    protected final void onRenderContainer(ComponentRenderingContext renderingContext) {
        Optional<LicketComponent<?>> parent = traverseUp(component -> component instanceof AbstractLicketMultiContainer);
        if (!parent.isPresent()) {
            return;
        }
        AbstractLicketMultiContainer parentContainer = (AbstractLicketMultiContainer) parent.get();
        renderingContext.onSurfaceElement(element -> {
            String firstPart = "model";
            if (!parentContainer.getView().hasTemplate()) {
                firstPart = parentContainer.getId();
            }
            element.addAttribute("v-for", format("%s in %s.%s", getId(), firstPart, getComponentModel().get()));
        });
    }
}
