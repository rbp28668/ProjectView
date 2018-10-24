/*
 * DirectoryWalker.java
 * Created on 25-Oct-2004
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * DirectoryWalker
 * @author Bruce.Porteous
 *
 */
public class DirectoryWalker {


	private final static DirectoryFileFilter acceptDirectories = new DirectoryFileFilter();
	private final ExtensionFileFilter acceptFiles = new ExtensionFileFilter();
	
	private boolean depthFirst = false;
	private List visitors = new LinkedList();
	
	/**
	 * 
	 */
	public DirectoryWalker() {
		super();
	}
	
	/**
	 * Adds a visitor that will be called when a file or directory
	 * is visited.
	 * @param visitor is the visitor to be added, must not be null.
	 */
	public void addVisitor(FileVisitor visitor){
		if(visitor == null){
			throw new NullPointerException("Adding a null file visitor to DirectoryWalker");
		}
		visitors.add(visitor);
	}
	
	/**
	 * Removes a visitor from the callback list.
	 * @param visitor is the visitor to remove.
	 */
	public void removeVisitor(FileVisitor visitor){
		visitors.remove(visitor);
	}
	
	/**
	 * Sets a file extension so only files with that extension
	 * will be reported.
	 * @param ext is the extension to set.
	 */
	public void setExtension(String ext){
		acceptFiles.setExtension(ext);
	}
	
	/**
	 * Sets a user-defined file filter to filter the list of
	 * files returned.  Setting the filter over-rides any
	 * extension settings.  The filter does not need to check
	 * explicitly for files (as distinct from directories).
	 * @param filter is the filter to set.
	 */
	public void setFilter(FileFilter filter){
		acceptFiles.setFilter(filter);
	}
	
	/**
	 * Polls a given source directory recursively traverses a directory.
	 * @param sourceDir is the directory to poll.
	 * @throws IOException
	 */
	public void pollDirectory(String sourceDir) throws IOException {
		pollDirectory(new File(sourceDir));
	}
	
	/**
	 * Polls a given source directory recursively traverses a directory.
	 * @param sourceDir is the directory to poll.
	 * @throws IOException
	 */
	public void pollDirectory(File sourceDir) throws IOException  {
		if(!sourceDir.exists()){
			throw new IOException("Source directory " + sourceDir.getAbsolutePath() + " does not exist");
		}
		
		if(!sourceDir.isDirectory()){
			throw new IOException("Source " + sourceDir.getAbsolutePath() + " is not a directory");
		}
		
		fireOnEnterDirectory(sourceDir);
		
		if(depthFirst){
			processDirectories(sourceDir);
			processFiles(sourceDir);
		} else {
			processFiles(sourceDir);
			processDirectories(sourceDir);
		}
		fireOnLeaveDirectory(sourceDir);
		
	}
	
	/**
     * @param sourceDir
     * @throws IOException
     */
    private void processDirectories(File sourceDir) throws IOException {
        File[] dirs = sourceDir.listFiles(acceptDirectories);
		if(dirs != null){
			for(int i=0; i<dirs.length; ++i){
				pollDirectory(dirs[i]);
			}
		}
    }

    /**
     * @param sourceDir
     * @throws IOException
     */
    private void processFiles(File sourceDir) throws IOException {
        File[] files = sourceDir.listFiles(acceptFiles);
		
		if(files != null){
			for(int i=0; i<files.length; ++i){
				fireOnFile(files[i]);
			}
		}
    }

    /**
	 * Notify all visitors of a file.
	 * @param file
	 */
	private void fireOnFile(File file) throws IOException{
		for(Iterator iter = visitors.iterator(); iter.hasNext();){
			FileVisitor visitor = (FileVisitor)iter.next();
			visitor.onFile(file);
		}
	}
	
	/**
	 * Notify all visitors of entering a directory.
	 * @param file
	 */
	private void fireOnEnterDirectory(File file)  throws IOException{
		for(Iterator iter = visitors.iterator(); iter.hasNext();){
			FileVisitor visitor = (FileVisitor)iter.next();
			visitor.onEnterDirectory(file);
		}
	}

	/**
	 * Notify all visitors of leaving a directory.
	 * @param file
	 */
	private void fireOnLeaveDirectory(File file)  throws IOException{
		for(Iterator iter = visitors.iterator(); iter.hasNext();){
			FileVisitor visitor = (FileVisitor)iter.next();
			visitor.onLeaveDirectory(file);
		}
	}

	/**
	 * ExtensionFileFilter - only accepts files.
	 * Can be extended by setting an extension in which case it
	 * only accepts files with that extension.
	 * Can also be extended by adding a filter in which case it
	 * only accepts files that match that filter.
	 * 
	 */
	private static class ExtensionFileFilter implements FileFilter{
		
		private FileFilter userFilter = null;
		private String extension = null;
		
		/**
		 * Sets an extension to match against.
		 * @param ext
		 */
		public void setExtension(String ext){
			extension = ext;
		}
		
		/**
		 * Sets a user defined FileFilter to 
		 * @param filter
		 */
		public void setFilter(FileFilter filter){
			userFilter = filter;
		}
		
		/* (non-Javadoc)
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File path){
			if(! path.isFile()) {
				return false;
			}
			
			if(userFilter != null){
				return userFilter.accept(path);
			}
			
			if(extension != null){
				return path.getName().endsWith(extension);
			}
			
			return true;
		}
	}

	/**
	 * DirectoryFileFilter - only accepts files that are directories.
	 * Used for recursive directory traversal.
	 */
	private static class DirectoryFileFilter implements FileFilter{
		public boolean accept(File path){
			return path.isDirectory();
		}
	}
	

    /**
     * @return Returns the depthFirst.
     */
    public boolean isDepthFirst() {
        return depthFirst;
    }
    /**
     * @param depthFirst The depthFirst to set.
     */
    public void setDepthFirst(boolean depthFirst) {
        this.depthFirst = depthFirst;
    }
}
