/*
 * BooleanHandler.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;

import java.math.BigDecimal;

class BigDecimalHandler extends TypeHandler{

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#parse(java.lang.String)
	 */
	public Object parse(String value) {
	  if(value.trim().isEmpty()) {
	    return null;
	  } else {
		return new BigDecimal(value);
	  }
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#getTargetClass()
	 */
	public Class<?> getTargetClass() {
		return BigDecimal.class;
	}

    @Override
    public int getDefaultLength() {
        return 10;
    }
	
}