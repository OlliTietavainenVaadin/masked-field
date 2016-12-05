package org.vaadin.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Event;
import com.vaadin.client.ui.VTextField;
import org.vaadin.shared.Constants;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 *
 * Based on DecimalField by @author Eduardo Frazao
 *
 */
public class CurrencyFieldWidget extends VTextField implements KeyPressHandler, BlurHandler, FocusHandler {
	
	
	private char decimalSeparator;
	private char groupingSeparator;
	private String mask;
	
	private Number maxValue = Double.MAX_VALUE;
	
	private NumberFormat formatter;

	protected static char[] acceptedCharSet = {
		(char) KeyCodes.KEY_BACKSPACE,
		(char) KeyCodes.KEY_TAB,
		(char) KeyCodes.KEY_DELETE,  
		(char) KeyCodes.KEY_END,
		(char) KeyCodes.KEY_ENTER,
		(char) KeyCodes.KEY_ESCAPE,
		(char) KeyCodes.KEY_HOME,
		(char) KeyCodes.KEY_LEFT,
		(char) KeyCodes.KEY_PAGEDOWN,
		(char) KeyCodes.KEY_PAGEUP,
		(char) KeyCodes.KEY_RIGHT
	};
	
	static {
		Arrays.sort(acceptedCharSet);
	}

	public CurrencyFieldWidget() {
		setAlignment(TextAlignment.RIGHT);

		addKeyPressHandler(this);
		addKeyDownHandler(this);
		sinkEvents(Event.ONPASTE);
		
		NumberFormat.setForcedLatinDigits(false);
		formatter = NumberFormat.getFormat("#,###.00");
	}
	
	private boolean isCopyOrPasteEvent(KeyPressEvent evt) {
		if(evt.isControlKeyDown()) {
			return Character.toString(evt.getCharCode()).toLowerCase().equals("c") ||
					Character.toString(evt.getCharCode()).toLowerCase().equals("v");
		}
		return false;
	}
	
	private boolean isAcceptedKey(char charCode) {
		if(charCode == groupingSeparator) {
			return false;
		}
		if(charCode == decimalSeparator) {
			if(!mask.contains(Character.toString(Constants.FIXED_LOCALE_DECIMAL_SEPARATOR))) {
				return false;
			}
			String str = getText().trim();
			if(!str.isEmpty()) {
				return !str.contains(Character.toString(decimalSeparator));
			} else {
				return false;
			}
		}
		return Character.isDigit(charCode)
				|| charCode == decimalSeparator 
				|| Arrays.binarySearch(acceptedCharSet, charCode) >= 0;
	}
	
	@Override
	public void onKeyPress(KeyPressEvent event) {
		if(!isCopyOrPasteEvent(event)) {
			if (event.getCharCode() != Constants.EMPTY_CHAR && !isAcceptedKey(event.getCharCode()) ) {
				cancelKey();
			} else if(!getText().trim().isEmpty() && formatter.parse(getText()) >= maxValue.doubleValue()) {
				cancelKey();
			}
		}
	}
	
	
	@Override
	public void onKeyDown(KeyDownEvent event) {
		super.onKeyDown(event);
		if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			refreshValue();
		}
	}

	@Override
	public void onBlur(BlurEvent event) {
		super.onBlur(event);
		refreshValue();
	}
	
	@Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);
        if (event.getTypeInt() == Event.ONPASTE) {
            onPaste(event);
        }
    }
	
	public void onPaste(Event event) {
		refreshValue();
	}

	private void refreshValue() {
		super.setText(reformatContent(null));
	}
	
	private void refreshValue(String withFormatedValue) {
		super.setText(reformatContent(withFormatedValue));
	}
	
	protected String reformatContent(String value) {
	    String str = value == null ? getText() : value;
	    if(!str.trim().isEmpty()) {
            BigDecimal amountBD = new BigDecimal(readDoubleFromFormattedValue(value));
            double amount = amountBD.doubleValue();
            if (!str.contains(Character.toString(Constants.FIXED_LOCALE_DECIMAL_SEPARATOR))) {
                amount = amountBD.movePointLeft(2).doubleValue();
            }
            String formatted = formatter.format(amount);
            String afterSeparators = replaceSeparators(formatted);
            return afterSeparators;
        }
	    return str;
	  }

	protected double readDoubleFromFormattedValue(String value) {
		String str = (value == null || value.trim().isEmpty()) ? getText().trim() : value;
		if(groupingSeparator != Constants.EMPTY_CHAR) {
			str = str.replaceAll(groupingSeparator == '.' ? "\\." : Character.toString(groupingSeparator), "");
		}
		if(decimalSeparator != Constants.EMPTY_CHAR) {
			if(decimalSeparator != '.') {
				str = str.replaceAll(Character.toString(decimalSeparator), ".");
			}
		}
		try {
			return Double.parseDouble(str);
		} catch (NumberFormatException ex) {
			return 0.0;
		}
	}
	
	protected String replaceSeparators(final String defaultFormatedValue) {
		String str = new String(defaultFormatedValue);
		if (str.startsWith(Character.toString(Constants.FIXED_LOCALE_DECIMAL_SEPARATOR))) {
		    str = "0" + str;
        }
		if(groupingSeparator != Constants.EMPTY_CHAR) {
			str = str.replaceAll(Character.toString(Constants.FIXED_LOCALE_GROUPING_SEPARATOR), Character.toString(groupingSeparator));
		}
		if(decimalSeparator != Constants.EMPTY_CHAR) {
			if (defaultFormatedValue.contains(Character.toString(Constants.FIXED_LOCALE_DECIMAL_SEPARATOR))) {
				return str;
			}
			if(mask.contains(Character.toString(Constants.FIXED_LOCALE_DECIMAL_SEPARATOR))) {
				int decimalLenght = mask.length() - mask.indexOf(Constants.FIXED_LOCALE_DECIMAL_SEPARATOR) - 1;
				StringBuilder sb = new StringBuilder(str);
				sb.insert(str.length() - decimalLenght , decimalSeparator);
				str = sb.toString();
			}
		}
		return str;
	}

	public char getDecimalSeparator() {
		return decimalSeparator;
	}

	public void setDecimalSeparator(char decimalSeparator) {
		if(decimalSeparator != this.decimalSeparator) {
			this.decimalSeparator = decimalSeparator;
			refreshValue();
		}
	}

	public char getGroupingSeparator() {
		return groupingSeparator;
	}

	public void setGroupingSeparator(char groupingSeparator) {
		if(groupingSeparator != this.groupingSeparator) {
			this.groupingSeparator = groupingSeparator;
			refreshValue();
		}
	}

	public String getMask() {
		return mask;
	}


	@Override
	public void setText(String value) {
		if(value == null) {
			super.setText(null);
		} else {
			refreshValue(value);
		}
	}

    @Override
    public void onFocus(FocusEvent event) {
        super.selectAll();
	    super.onFocus(event);
    }
}
