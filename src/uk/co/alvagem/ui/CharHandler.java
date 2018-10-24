/*
 * CharHandler.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;


class CharHandler extends TypeHandler {

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#parse(java.lang.String)
	 */
	public Object parse(String value) {
		Character ch = null;
		if(value != null && value.length() > 0){
			ch = new Character(value.charAt(0));
		}
		return ch;
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#getTargetClass()
	 */
	public Class<?> getTargetClass() {
		return Character.TYPE;
	}

    @Override
    public int getDefaultLength() {
        return 1;
    }
}