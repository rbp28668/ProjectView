/*
 * UncertaintyType.java
 * Created on 28-Apr-2005
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.projectview.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.GammaDistribution;
import org.apache.commons.math.distribution.GammaDistributionImpl;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;

/**
 * UncertaintyType models the different type of uncertainty
 * in a project.
 * @author Bruce.Porteous
 */
public class UncertaintyType {

    /** Database ID */
    private Integer id;
    
    /** name of type */
	private String type;
	
	public final static UncertaintyType NONE = new UncertaintyType("NONE");
	public final static UncertaintyType LINEAR = new LinearUncertaintyType("LINEAR");
	public final static UncertaintyType GAUSSIAN = new GaussianUncertaintyType("GAUSSIAN");
	public final static UncertaintyType GAMMA = new GammaUncertaintyType("GAMMA");
	
	private final static Map<String,UncertaintyType>types = new HashMap<String,UncertaintyType>();
	static {
	    types.put(NONE.toString(), NONE);
	    types.put(LINEAR.toString(), LINEAR);
	    types.put(GAUSSIAN.toString(), GAUSSIAN);
	    types.put(GAMMA.toString(),GAMMA);
	}
	
	public static UncertaintyType lookup(String name){
	    return types.get(name);
	}

	
	/**
	 * Gets the list of uncertainty types.
	 * @return
	 */
	public static Collection<UncertaintyType> getTypes(){
		return Collections.unmodifiableCollection(types.values());
	}
	
	/**
	 * 
	 */
	private UncertaintyType(String type) {
		super();
		this.type = type;
	}

    /**
     * @return Returns the id.
     * @hibernate.id generator-class="native"
     */
    public Integer getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
    	return type;
    }
    
    public float getEstimatedEffort(float raw, float param, float random) throws MathException{
    	return raw;
    }
    
    /**
     * @author bruce.porteous
     *
     */
    private static class LinearUncertaintyType extends UncertaintyType {
    	
    	LinearUncertaintyType(String name){
    		super(name);
    	}
    	
        public float getEstimatedEffort(float raw, float param, float random){
        	return raw + param * random;
        }

    }
    
    /**
     * See Gamma distribution.  Scale and 
     * @author bruce.porteous
     *
     */
    private static class GaussianUncertaintyType extends UncertaintyType {
    	
    	private NormalDistribution distribution;
    	
    	GaussianUncertaintyType(String name){
    		super(name);
    		
    		distribution = new NormalDistributionImpl();
    	}
    	
        public float getEstimatedEffort(float raw, float param, float random) throws MathException{
        	
        	// Want a distribution centred around the mean.  Parameter sets the width
        	// of the distribution.
        	distribution.setMean(raw);
        	distribution.setStandardDeviation(param);
        	
        	// Want the inverse cumulative distribution function - how long will it take to
        	// have a "random" probability of completion?
        	float value = (float) distribution.inverseCumulativeProbability(random);
        	if( value < 0){
        		value = 0;
        	}
        	return value;
        }

    }
    /**
     * @author bruce.porteous
     *
     */
    private static class GammaUncertaintyType extends UncertaintyType {
    	
    	private GammaDistribution distribution;
    	
    	GammaUncertaintyType(String name){
    		super(name);
    		
    		distribution = new GammaDistributionImpl(2,2); // Roughly right shape, peak at 2.
    	}
    	
        public float getEstimatedEffort(float raw, float param, float random) throws MathException{
        	// Want the inverse cumulative distribution function - how long will it take to
        	// have a "random" probability of completion?
        	// Note "magic" number 2 - scales so that the peak of the distribution is at 1.
        	float range = (float) distribution.inverseCumulativeProbability(random) / 2;
        	float value = raw + param * range;
        	if( value < 0){
        		value = 0;
        	}
        	return value;
        }

    }


}
