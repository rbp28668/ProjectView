/*
 * LongHandler.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;


class LongHandler extends TypeHandler {

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#parse(java.lang.String)
	 */
	public Object parse(String value) {
		Long i = null;
		try{
			i = Long.valueOf(value);
		} catch (Exception e){
			// NOP
		}
		return i;
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#getTargetClass()
	 */
	public Class<?> getTargetClass() {
		return Long.TYPE;
	}

    @Override
    public int getDefaultLength() {
        return 20;
    }
	
}