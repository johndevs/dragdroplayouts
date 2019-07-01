/*
 * Copyright 2015 John Ahlroos
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package fi.jasoft.dragdroplayouts.v7.drophandlers;

import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.shared.ui.dd.VerticalDropLocation;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.v7.ui.VerticalLayout;

import fi.jasoft.dragdroplayouts.v7.DDVerticalLayout
        .VerticalLayoutTargetDetails;
import fi.jasoft.dragdroplayouts.drophandlers
        .AbstractDefaultLayoutDropHandler;
import fi.jasoft.dragdroplayouts.events.LayoutBoundTransferable;

/**
 * A default drop handler for vertical layouts
 * 
 * @author John Ahlroos / www.jasoft.fi
 * @since 0.6.0
 */
@SuppressWarnings("serial")
public class DefaultVerticalLayoutDropHandler
        extends AbstractDefaultLayoutDropHandler {

    private Alignment dropAlignment;

    /**
     * Constructor
     */
    public DefaultVerticalLayoutDropHandler() {

    }

    /**
     * Constructor
     * 
     * @param dropCellAlignment
     *            The cell alignment of the component after it has been dropped
     */
    public DefaultVerticalLayoutDropHandler(Alignment dropCellAlignment) {
        this.dropAlignment = dropCellAlignment;
    }

    /**
     * Called when a component changed location within the layout
     * 
     * @param event
     *            The drag and drop event
     */
    @Override
    protected void handleComponentReordering(DragAndDropEvent event) {
        // Component re-ordering
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        VerticalLayoutTargetDetails details = (VerticalLayoutTargetDetails) event
                .getTargetDetails();
        AbstractOrderedLayout layout = (AbstractOrderedLayout) details
                .getTarget();
        Component comp = transferable.getComponent();
        int idx = details.getOverIndex();
        int oldIndex = layout.getComponentIndex(comp);

        if (idx == oldIndex) {
            // Index did not change
            return;
        }

        // Detach
        layout.removeComponent(comp);

        // Account for detachment if new index is bigger then old index
        if (idx > oldIndex) {
            idx--;
        }

        // Increase index if component is dropped after or above a previous
        // component
        VerticalDropLocation loc = details.getDropLocation();
        if (loc == VerticalDropLocation.MIDDLE
                || loc == VerticalDropLocation.BOTTOM) {
            idx++;
        }

        // Add component
        if (idx >= 0) {
            layout.addComponent(comp, idx);
        } else {
            layout.addComponent(comp);
        }

        // Add component alignment if given
        if (dropAlignment != null) {
            layout.setComponentAlignment(comp, dropAlignment);
        }

    }

    /**
     * Handle a drop from another layout
     * 
     * @param event
     *            The drag and drop event
     */
    @Override
    protected void handleDropFromLayout(DragAndDropEvent event) {
        LayoutBoundTransferable transferable = (LayoutBoundTransferable) event
                .getTransferable();
        VerticalLayoutTargetDetails details = (VerticalLayoutTargetDetails) event
                .getTargetDetails();
        AbstractOrderedLayout layout = (AbstractOrderedLayout) details
                .getTarget();
        Component source = event.getTransferable().getSourceComponent();
        int idx = (details).getOverIndex();
        Component comp = transferable.getComponent();

        // Check that we are not dragging an outer layout into an inner
        // layout
        Component parent = layout.getParent();
        while (parent != null) {
            if (parent == comp) {
                return;
            }
            parent = parent.getParent();
        }

        // Detach from old source
        if (source instanceof ComponentContainer) {
            ((ComponentContainer) source).removeComponent(comp);
        } else if (source instanceof SingleComponentContainer) {
            ((SingleComponentContainer) source).setContent(null);
        }

        // Increase index if component is dropped after or above a
        // previous
        // component
        VerticalDropLocation loc = (details).getDropLocation();
        if (loc == VerticalDropLocation.MIDDLE
                || loc == VerticalDropLocation.BOTTOM) {
            idx++;
        }

        // Add component
        if (idx >= 0) {
            layout.addComponent(comp, idx);
        } else {
            layout.addComponent(comp);
        }

        // Add component alignment if given
        if (dropAlignment != null) {
            layout.setComponentAlignment(comp, dropAlignment);
        }
    }

    @Override
    protected void handleHTML5Drop(DragAndDropEvent event) {
        VerticalLayoutTargetDetails details = (VerticalLayoutTargetDetails) event
                .getTargetDetails();
        AbstractOrderedLayout layout = (AbstractOrderedLayout) details
                .getTarget();
        int idx = (details).getOverIndex();

        // Increase index if component is dropped after or above a
        // previous
        // component
        VerticalDropLocation loc = (details).getDropLocation();
        if (loc == VerticalDropLocation.MIDDLE
                || loc == VerticalDropLocation.BOTTOM) {
            idx++;
        }

        Component comp = resolveComponentFromHTML5Drop(event);

        // Add component
        if (idx >= 0) {
            layout.addComponent(comp, idx);
        } else {
            layout.addComponent(comp);
        }

        // Add component alignment if given
        if (dropAlignment != null) {
            layout.setComponentAlignment(comp, dropAlignment);
        }

    }

    @Override
    public Class<VerticalLayout> getTargetLayoutType() {
        return VerticalLayout.class;
    }
}
