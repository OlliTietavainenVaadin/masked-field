package org.vaadin.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.textfield.TextFieldConnector;
import com.vaadin.shared.ui.Connect;
import org.vaadin.CurrencyField;

@Connect(CurrencyField.class)
public class CurrencyFieldConnector extends TextFieldConnector {

	private static final long serialVersionUID = 1L;

	@Override
	protected Widget createWidget() {
		return GWT.create(CurrencyFieldWidget.class);
	}
	
	@Override
	public CurrencyFieldWidget getWidget() {
		return (CurrencyFieldWidget) super.getWidget();
	}

	@Override
	public CurrencyFieldState getState() {
		return (CurrencyFieldState) super.getState();
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		getWidget().setGroupingSeparator(getState().groupingSeparator);
		getWidget().setDecimalSeparator(getState().decimalSeparator);
		super.onStateChanged(stateChangeEvent);
	}
	
}
