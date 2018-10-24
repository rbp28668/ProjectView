/*
 * DoubleHandler.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;


class DoubleHandler extends TypeHandler {

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#parse(java.lang.String)
	 */
	public Object parse(String value) {
		Double d = null;
		try{
			d = Double.valueOf(value);
		} catch (Exception e){
			// NOP
		}
		return d;
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#getTargetClass()
	 */
	public Class<?> getTargetClass() {
		return Double.TYPE;
	}

    @Override
    public int getDefaultLength() {
        return 26;  // I think: -1.79769313486231570e+308
    }
	
}