/*
 * IntegerHandler.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;


class IntegerHandler extends TypeHandler {

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#parse(java.lang.String)
	 */
	public Object parse(String value) {
		Integer i = null;
		try {
			i = Integer.valueOf(value);
		}catch (Exception e) {
			// NOP
		}
		return i;
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#getTargetClass()
	 */
	public Class<?> getTargetClass() {
		return Integer.TYPE;
	}

    @Override
    public int getDefaultLength() {
        return 11;
    }
}