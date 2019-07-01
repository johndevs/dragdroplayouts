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
package fi.jasoft.dragdroplayouts.demo;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

import de.java2html.converter.JavaSource2HTMLConverter;
import de.java2html.javasource.JavaSource;
import de.java2html.javasource.JavaSourceParser;
import de.java2html.options.JavaSourceConversionOptions;
import de.java2html.util.IllegalConfigurationException;
import fi.jasoft.dragdroplayouts.demo.views.*;

@Theme("demo")
public class DemoUI extends UI {

    private Navigator navigator;

    private Grid<DemoView> selection;

    private final List<DemoView> views = new ArrayList<>();

    private Label codeLabel = new Label("", ContentMode.HTML);

    @Override
    protected void init(VaadinRequest request) {

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        setContent(content);

        Label header = new Label("DragDropLayouts for Vaadin 8");
        header.setStyleName(ValoTheme.LABEL_H1);
        content.addComponent(header);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        content.addComponent(hl);
        content.setExpandRatio(hl, 1);

        VerticalSplitPanel split = new VerticalSplitPanel();
        hl.addComponent(split);
        hl.setExpandRatio(split, 1);

        CssLayout placeHolder = new CssLayout(new Label("No view selected."));
        placeHolder.setSizeFull();
        split.setFirstComponent(placeHolder);

        Panel codePanel = new Panel(codeLabel);
        codePanel.setSizeFull();
        split.setSecondComponent(codePanel);

        navigator = new Navigator(this, placeHolder);

        navigator.addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                DemoView view = (DemoView) event.getNewView();
                selection.getSelectionModel().select(view);
                codeLabel.setValue(getFormattedSourceCode(view.getSource()));
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
                // TODO Auto-generated method stub

            }
        });

        try {
            addView(new DragdropAbsoluteLayoutDemo(navigator));
            addView(new DragdropVerticalLayoutDemo(navigator));
            addView(new DragdropHorizontalLayoutDemo(navigator));
            addView(new DragdropGridLayoutDemo(navigator));
            addView(new DragdropCssLayoutDemo(navigator));
            addView(new DragdropFormLayoutDemo(navigator));
            addView(new DragdropPanelDemo(navigator));

            addView(new DragdropLayoutDraggingDemo(navigator));
            addView(new DragdropHorizontalSplitPanelDemo(navigator));
            addView(new DragdropVerticalSplitPanelDemo(navigator));
            addView(new DragdropTabsheetDemo(navigator));
            addView(new DragdropAccordionDemo(navigator));

            addView(new DragdropDragFilterDemo(navigator));
            addView(new DragdropDragGrabFilterDemo(navigator));
            addView(new DragdropCaptionModeDemo(navigator));

            addView(new DragdropV7VerticalLayoutDemo(navigator));
            addView(new DragdropV7HorizontalLayoutDemo(navigator));

            addView(new DragdropDragCaptionDemo(navigator));

            // addView(new DragdropIframeDragging(navigator));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        hl.addComponent(selection = createViewSelection(), 0);

        String fragment = Page.getCurrent().getUriFragment();
        if (fragment == null || fragment.equals("")) {
            navigator.navigateTo(DragdropAbsoluteLayoutDemo.NAME);
        }
    }

    private void addView(DemoView view) throws IllegalArgumentException,
            IllegalAccessException, NoSuchFieldException, SecurityException {
        String name = view.getClass().getDeclaredField("NAME").get(view)
                .toString();
        navigator.addView(name, view);
        views.add(view);
    }

    private Grid<DemoView> createViewSelection() {
        Grid<DemoView> select = new Grid<>();

        select.addColumn(view -> view.getCaption());
        select.setWidth("200px");
        select.setHeight("100%");
        select.addSelectionListener((change) ->
                navigator.navigateTo(change.getFirstSelectedItem().get().getName())
        );

        select.setItems(views);
        return select;
    }

    public String getFormattedSourceCode(String sourceCode) {
        try {
            JavaSource source = new JavaSourceParser()
                    .parse(new StringReader(sourceCode));
            JavaSource2HTMLConverter converter = new JavaSource2HTMLConverter();
            StringWriter writer = new StringWriter();
            JavaSourceConversionOptions options = JavaSourceConversionOptions
                    .getDefault();
            options.setShowLineNumbers(true);
            options.setAddLineAnchors(false);
            converter.convert(source, options, writer);

            return writer.toString();
        } catch (IllegalConfigurationException exception) {
            return sourceCode;
        } catch (IOException exception) {
            return sourceCode;
        }
    }
}