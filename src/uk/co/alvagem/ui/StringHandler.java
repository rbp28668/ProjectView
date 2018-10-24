/*
 * StringHandler.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;


class StringHandler extends TypeHandler{

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#parse(java.lang.String)
	 */
	public Object parse(String value) {
		return value;
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#getTargetClass()
	 */
	public Class<?> getTargetClass() {
		return String.class;
	}

    @Override
    public int getDefaultLength() {
        return -1; // No sensible default length for string.
    }
	
}