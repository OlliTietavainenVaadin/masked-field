package org.vaadin;

import com.vaadin.ui.Component;
import org.vaadin.addonhelpers.AbstractTest;

/**
 * Created by Olli on 5.12.2016.
 */
public class CurrencyFieldUsageUI extends AbstractTest {
    @Override
    public Component getTestComponent() {
        CurrencyField df = new CurrencyField('.', ' ');
        return df;
    }
}
