/*
 * ShortHandler.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;


class ShortHandler extends TypeHandler {

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#parse(java.lang.String)
	 */
	public Object parse(String value) {
		Short i = null;
		try{
			i = Short.valueOf(value);
		} catch (Exception e){
			// NOP
		}
		return i;
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#getTargetClass()
	 */
	public Class<?> getTargetClass() {
		return Short.TYPE;
	}

    @Override
    public int getDefaultLength() {
        return 6;
    }
	
}