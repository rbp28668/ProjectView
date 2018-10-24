/*
 * FloatHandler.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;


class FloatHandler extends TypeHandler {

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#parse(java.lang.String)
	 */
	public Object parse(String value) {
		Float f = null;
		try{
			f = Float.valueOf(value);
		} catch (Exception e){
			// NOP
		}
		return f;
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#getTargetClass()
	 */
	public Class<?> getTargetClass() {
		return Float.TYPE;
	}

    @Override
    public int getDefaultLength() {
        return 15; // -3.40282347e+38
    }
	
}