/*
 * BooleanHandler.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;


class BooleanHandler extends TypeHandler{

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#parse(java.lang.String)
	 */
	public Object parse(String value) {
		boolean b = value != null && (value.equals("on") || value.equals("true"));
		return Boolean.valueOf(b);
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#getTargetClass()
	 */
	public Class<?> getTargetClass() {
		return Boolean.TYPE;
	}

    @Override
    public int getDefaultLength() {
        return 5;
    }
	
}