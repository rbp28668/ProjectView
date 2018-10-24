/*
 * MilestoneRole.java
 * Created on 28-Apr-2005
 * By Bruce.Porteous
 *
 */
package uk.co.alvagem.projectview.model;

/**
 * MilestoneRole
 * @author Bruce.Porteous
 */
public class MilestoneRole extends TaskRole {

    /** amount of any payment associated with this milestone */
    private float payment;
    
    /**
     * 
     */
    public MilestoneRole() {
        super();
    }

    /**
     * @return Returns the payment.
     */
    public float getPayment() {
        return payment;
    }
    /**
     * @param payment The payment to set.
     */
    public void setPayment(float payment) {
        this.payment = payment;
    }
}
