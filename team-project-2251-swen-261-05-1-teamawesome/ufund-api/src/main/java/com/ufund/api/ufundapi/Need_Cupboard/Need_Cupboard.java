package com.ufund.api.ufundapi.Need_Cupboard;
import com.ufund.api.ufundapi.Need.Need;

/*
 * Class representing a cupboard that holds Needs
 */
public class Need_Cupboard {
    private Need[] needs;

    /*
     * Constructor for Need_Cupboard
     * @param needs An array of Need to initialize the cupboard with
     */
    public Need_Cupboard(Need[] needs){
        this.needs=needs;
    }

    /*
     * Gets the needs in the cupboard
     * @return An array of Need representing the needs in the cupboard
     */
    public  Need[] getNeeds() {
        return needs;
    }

    /*
     * Sets the needs in the cupboard
     * @param needs An array of Need to set the cupboard's needs to
     */
    public void setNeeds(Need[] needs) {
        this.needs = needs;
    }
}
