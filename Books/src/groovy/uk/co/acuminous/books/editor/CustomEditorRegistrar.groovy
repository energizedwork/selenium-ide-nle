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
