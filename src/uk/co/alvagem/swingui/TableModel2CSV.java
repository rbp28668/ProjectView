/*
 * TableModel2CSV.java
 * Project: ProjectView
 * Created on 18 Jan 2008
 *
 */
package uk.co.alvagem.swingui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.swing.table.TableModel;

/**
 * TableModel2CSV is a utility class to export a TableModel to a CSV file.
 * 
 * @author rbp28668
 */
public class TableModel2CSV {

    private TableModel model;
    private String separator = ",";
    private String delimiter = "\"";
    private boolean exportHeader = true;
    
    
    public TableModel2CSV(){
    }
    
    public TableModel2CSV(TableModel model){
        this.model = model;
    }
    
    /**
     * @return the model
     */
    public TableModel getModel() {
        return model;
    }



    /**
     * Sets the table model to export.
     * @param model the model to set
     */
    public void setModel(TableModel model) {
        this.model = model;
    }



    /**
     * @return the separator
     */
    public String getSeparator() {
        return separator;
    }



    /**
     * @param separator the separator to set
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }



    /**
     * @return the delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }



    /**
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }



    /**
     * @return the exportHeader
     */
    public boolean isExportHeader() {
        return exportHeader;
    }



    /**
     * @param exportHeader the exportHeader to set
     */
    public void setExportHeader(boolean exportHeader) {
        this.exportHeader = exportHeader;
    }



    /**
     * Exports the TableModel to a file with the given path.
     * @param path is the path to export to.
     * @throws IOException
     */
    public void export(String path) throws IOException{
        
        checkModel();  // before any file is created
        
        FileOutputStream fos = new FileOutputStream(path);
        try {
            export(fos);
        }finally {
            fos.close();
        }
    }

    /**
     * Exports the TableModel to an output stream.
     * @param os
     */
    public void export(OutputStream os){
        PrintWriter out = new PrintWriter(os);
        try {
            export(out);
        } finally {
            out.close();
        }
    }

    /**
     * Exports the TableModel to a PrintWriter.
     * @param out
     */
    public void export(PrintWriter out){
        checkModel();
        if(exportHeader){
            exportHeader(out);
        }
        int nRows = model.getRowCount();
        for(int iRow = 0; iRow < nRows; ++iRow){
            exportRow(iRow,out);
        }
    }

    private void checkModel(){
        if(model == null){
            throw new NullPointerException("Can't export a null TableModel");
        }
    }
    
    private void exportHeader(PrintWriter out){
        int nCols = model.getColumnCount();
        for(int iCol = 0; iCol < nCols; ++iCol){
            if(iCol > 0){
                out.print(separator);
            }
            out.print(delimiter);
            out.print(model.getColumnName(iCol));
            out.print(delimiter);
        }
        out.println();
    }


    private void exportRow(int iRow,PrintWriter out){
        int nCols = model.getColumnCount();
        for(int iCol = 0; iCol < nCols; ++iCol){
            Object value = model.getValueAt(iRow, iCol);
            if(iCol > 0){
                out.print(separator);
            }
            writeCell(value.toString(), out);
        }
        out.println();
    }
    
    private void writeCell(String value, PrintWriter out){
    	
    	if(value.indexOf('"') != -1){
    		int nChars = value.length();
    		StringBuffer buff = new StringBuffer(nChars + 8);
    		for(int i=0; i<nChars; ++i){
    			char ch = value.charAt(i);
    			if(ch == '"'){
    				buff.append('"');	// make double quote.
    			}
    			buff.append(ch);
    		}
    		value = buff.toString();
    	}
    	
        out.print(delimiter);
        out.print(value);
        out.print(delimiter);

    }
    
}
