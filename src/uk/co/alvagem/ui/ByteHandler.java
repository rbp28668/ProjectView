/*
 * ByteHandler.java
 * Created on 13-May-2005
 *
 */
package uk.co.alvagem.ui;


class ByteHandler extends TypeHandler {

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#parse(java.lang.String)
	 */
	public Object parse(String value) {
		Byte b = null;
		try{
			b = Byte.valueOf(value);
		} catch (Exception e){
			// NOP
		}
		return b;
	}

	/* (non-Javadoc)
	 * @see uk.co.alvagem.servlet.Form.TypeHandler#getTargetClass()
	 */
	public Class<?> getTargetClass() {
		return Byte.TYPE;
	}

    @Override
    public int getDefaultLength() {
        return 3;
    }
	
}