package com.ufund.api.ufundapi.Need;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Class representing a Need in the system
 */
public class Need {
    
    @JsonProperty("cost") private int cost;
    @JsonProperty("quantity") private int quantity;
    @JsonProperty("type") private String type;
    @JsonProperty("id") private final int id;

    /**
     * Default Constructor for {@linkplain Need Need} to create 
     * the object with Cost, quantity, type, and id
     * 
     * @param cost How much the {@link Need Need} costs
     * @param quantity How many are currently in inventory
     * @param type What kind of need it is
     * @param id Unique identifier for this need (Constant)
     */
    public Need (@JsonProperty("cost") int cost,
                 @JsonProperty("quantity") int quantity,
                 @JsonProperty("type") String type,
                 @JsonProperty("id") int id) {
        this.cost = cost;
        this.quantity = quantity;
        this.type = type;
        this.id = id;
    }

    /**
     * Gets the cost of the {@linkplain Need Need}
     * @return Cost of the {@linkplain Need Need}
     */
    public int getCost() {
        return cost;
    }

    /**
     * gets the quantity of this {@linkplain Need Need}
     * @return the quantity of the {@linkplain Need Need}
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * gets the type of {@linkplain Need Need}
     * @return The type of {@linkplain Need Need}
     */
    public String getType() {
        return type;
    }

    /**
     * changes the cost of the {@linkplain Need Need} to the parameter's value
     * @param cost The cost of the {@linkplain Need Need}
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * changes the quantity of the {@linkplain Need Need} to the parameter's value
     * @param quantity The quantity of the {@linkplain Need Need}
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Changes the type of the {@linkplain Need Need} to the value of the parameter
     * @param type The new type of the {@linkplain Need Need}
     */
    public void setType(String type) {
        this.type = type;
    }

    public void Update_Quantity(Need up_Need, int up_Quantity){

    }

    /**
     * Gets the {@linkplain Need Need}'s ID 
     * @return The {@link Need Need}'s ID
     */
    public int getId()
    {
        return this.id;
    }
}
