/*
 * Copyright 2014 John Ahlroos
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
package fi.jasoft.dragdroplayouts.client.ui.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DragEnterEvent;
import com.google.gwt.event.dom.client.DragEnterHandler;
import com.google.gwt.event.dom.client.DragLeaveEvent;
import com.google.gwt.event.dom.client.DragLeaveHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ComponentConnector;
import com.vaadin.client.ui.dd.VDragAndDropManager;
import com.vaadin.client.ui.dd.VDragEvent;
import com.vaadin.client.ui.dd.VTransferable;

import fi.jasoft.dragdroplayouts.client.ui.VDDAbstractDropHandler;

/**
 * Provides HTML5 drops for any connector
 * 
 * @author John Ahlroos / www.jasoft.fi
 */
public class HTML5Support {

  private final List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();

  public static class HTML5DragHandler implements DragEnterHandler, DragLeaveHandler,
      DragOverHandler, DropHandler {

    private VDragEvent vaadinDragEvent;

    private ComponentConnector connector;

    private VDDAbstractDropHandler<? extends Widget> dropHandler;

    public HTML5DragHandler(ComponentConnector connector,
        VDDAbstractDropHandler<? extends Widget> handler) {
      this.connector = connector;
      this.dropHandler = handler;
    }

    @Override
    public void onDrop(DropEvent event) {
      NativeEvent nativeEvent = event.getNativeEvent();
      if (nativeEvent != null && Element.is(nativeEvent.getEventTarget())) {
        nativeEvent.preventDefault();

        vaadinDragEvent.setCurrentGwtEvent(nativeEvent);

        // FIXME only text currently supported
        String data = event.getData("text/plain");
        vaadinDragEvent.getTransferable().setData("html5Data", data);

        if (dropHandler.drop(vaadinDragEvent)) {
          VDragAndDropManager.get().endDrag();
        } else {
          VDragAndDropManager.get().interruptDrag();
        }

        vaadinDragEvent = null;
      }
    }

    @Override
    public void onDragOver(DragOverEvent event) {
      NativeEvent nativeEvent = event.getNativeEvent();
      if (Element.is(nativeEvent.getEventTarget())) {
        Element target = Element.as(nativeEvent.getEventTarget());
        if (connector.getWidget().getElement().equals(target)) {
          nativeEvent.preventDefault();
          vaadinDragEvent.setCurrentGwtEvent(nativeEvent);
          dropHandler.dragOver(vaadinDragEvent);

        }
      }
    }

    @Override
    public void onDragLeave(DragLeaveEvent event) {
      NativeEvent nativeEvent = event.getNativeEvent();
      if (Element.is(nativeEvent.getEventTarget())) {
        Element target = Element.as(nativeEvent.getEventTarget());
        if (connector.getWidget().getElement().equals(target)) {
          nativeEvent.preventDefault();
          vaadinDragEvent.setCurrentGwtEvent(nativeEvent);
          dropHandler.dragLeave(vaadinDragEvent);
        }
      }
    }

    @Override
    public void onDragEnter(DragEnterEvent event) {
      NativeEvent nativeEvent = event.getNativeEvent();
      if (vaadinDragEvent == null && Element.is(nativeEvent.getEventTarget())) {
        Element target = Element.as(nativeEvent.getEventTarget());

        nativeEvent.preventDefault();
        nativeEvent.stopPropagation();

        VTransferable transferable = new VTransferable();
        transferable.setDragSource(connector);
        vaadinDragEvent =
            VDragAndDropManager.get().startDrag(transferable, event.getNativeEvent(), false);

        vaadinDragEvent.setCurrentGwtEvent(nativeEvent);
        dropHandler.dragEnter(vaadinDragEvent);
      }
    }
  }

  public static final HTML5Support enable(final ComponentConnector connector,
      final VDDAbstractDropHandler<? extends Widget> handler) {
    if (handler == null) {
      return null;
    }

    Widget w = connector.getWidget();
    final HTML5Support support = GWT.create(HTML5Support.class);
    final HTML5DragHandler dragHandler = new HTML5DragHandler(connector, handler);

    support.handlers.add(w.addDomHandler(dragHandler, DragEnterEvent.getType()));
    support.handlers.add(w.addDomHandler(dragHandler, DragLeaveEvent.getType()));
    support.handlers.add(w.addDomHandler(dragHandler, DragOverEvent.getType()));
    support.handlers.add(w.addDomHandler(dragHandler, DropEvent.getType()));

    return support;
  }

  private HTML5Support() {
    // Factory
  }

  public void disable() {
    for (HandlerRegistration handlerRegistration : handlers) {
      handlerRegistration.removeHandler();
    }
    handlers.clear();
  }
}
