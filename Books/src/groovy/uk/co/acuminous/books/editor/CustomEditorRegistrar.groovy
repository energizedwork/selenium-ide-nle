/*
 * Copyright 2010 Acuminous Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.acuminous.books.editor

import org.springframework.beans.PropertyEditorRegistry
import org.springframework.beans.PropertyEditorRegistrar
import org.springframework.beans.propertyeditors.CustomNumberEditor
import java.text.DecimalFormat


class CustomEditorRegistrar implements PropertyEditorRegistrar  {
    void registerCustomEditors(PropertyEditorRegistry registry) {
        DecimalFormat format = new DecimalFormat('#.#%')
        CustomNumberEditor percentageEditor = new CustomNumberEditor(BigDecimal, format, true)
        registry.registerCustomEditor(BigDecimal, 'chargeable', percentageEditor);
        registry.registerCustomEditor(BigDecimal, 'payable', percentageEditor);        
    }
}
