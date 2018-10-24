/**
 * 
 */
package uk.co.alvagem.projectview.core.filters;

public class Comparison{
	String name;
	boolean isLT;
	boolean isEQ;
	boolean isGT;
	
	public final static Comparison LT = new Comparison("<", true, false, false);
	public final static Comparison GT = new Comparison(">", false, false, true);
	public final static Comparison EQ = new Comparison("=", false, true, false);
	public final static Comparison LE = new Comparison("<=", true, true, false);
	public final static Comparison GE = new Comparison(">=", false, true, true);
	
	public final static Comparison[] COMPARISONS = { LT, GT, EQ, LE, GE };
	
	Comparison(String name, boolean isLT, boolean isEQ,	boolean isGT){
		this.name = name;
		this.isLT = isLT;
		this.isEQ = isEQ;
		this.isGT = isGT;
	}
	
	
	boolean accept(Comparable first, Object second){
		int sgn = first.compareTo(second);
		if(isEQ && sgn == 0) return true;
		if(isLT && sgn < 0) return true;
		if(isGT && sgn > 0) return true;
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return name;
	}
}