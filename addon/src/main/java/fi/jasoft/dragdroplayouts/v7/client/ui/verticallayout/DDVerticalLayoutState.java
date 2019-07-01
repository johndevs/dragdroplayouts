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
package fi.jasoft.dragdroplayouts.v7.client.ui.verticallayout;

import com.vaadin.shared.annotations.DelegateToWidget;
import com.vaadin.shared.ui.orderedlayout.VerticalLayoutState;

import fi.jasoft.dragdroplayouts.client.ui.interfaces.DDLayoutState;
import fi.jasoft.dragdroplayouts.client.ui.interfaces.DragAndDropAwareState;

public class DDVerticalLayoutState extends VerticalLayoutState
        implements DragAndDropAwareState {

    public static final float DEFAULT_VERTICAL_DROP_RATIO = 0.2f;

    @DelegateToWidget
    public float cellTopBottomDropRatio = DEFAULT_VERTICAL_DROP_RATIO;

    public DDLayoutState ddState = new DDLayoutState();

    @Override
    public DDLayoutState getDragAndDropState() {
        return ddState;
    }
}
