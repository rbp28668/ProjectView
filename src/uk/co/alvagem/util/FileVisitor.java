/*
 * FileVisitor.java
 * Created on 25-Oct-2004
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.util;

import java.io.File;
import java.io.IOException;

/**
 * FileVisitor
 * @author Bruce.Porteous
 *
 */
public interface FileVisitor {
	
	public void onFile(File file) throws IOException;
	public void onEnterDirectory(File dir)throws IOException;
	public void onLeaveDirectory(File dir)throws IOException;

}
