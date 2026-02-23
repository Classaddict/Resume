package com.ufund.api.ufundapi.Persistance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.ufund.api.ufundapi.Need.Need;

/**
 * Generic implementation of the {@linkplain NeedDAO NeedDAO} interface.<br><br>
 * All {@linkplain Need Needs} are stored in regular objects, nothing special<br><br><br><br>
 * 
 * This class is where all interactions with {@linkplain Need Need} objects happens
 * they are saved here, gotten from here, 
 * 
 * 
 * @author Bobby Orbin
 */
public class GenericNeedDAO implements NeedDAO
{
    //private static final Logger LOG = Logger.getLogger(GenericNeedDAO.class.getName());

    private static int nextId = 0;

    //Data type for storing all the needs in one place
    private final HashMap<Integer, Need> needs = new HashMap();


    /**
     * Default constructor for the GenericNeedDAO that takes in an array of {@linkplain Need Needs} to be put away to start
     * @param needsArray Array of {@linkplain Need Needs} to put into the collection of needs for accessing
     */
    public GenericNeedDAO(Need[] needsArray)
    {
        //putting all needs in the Hashmap
        for(Need need : needsArray)
        {
            this.needs.put(need.getId(), need);

            if(need.getId() > nextId)
                nextId = need.getId();
        }

        nextId++;
    }

    /**
     * gets the current NextId and returns that while incramenting it to keep it always acurate
     * @return The valid nextId
     */
    private int getNextID()
    {
        int newId = nextId;
        nextId++;

        return newId;
    }


    /*
     * Gets all Needs in the system
     * @return An array of all Needs in the system
     */
    @Override
    public Need[] getNeeds() throws IOException 
    {
        return (Need[])needs.values().toArray();
    }

    /*
     * Finds all Needs that contain the query string
     * @param query The query to search Needs for
     * @return An array of Needs that contain the query string
     */
    @Override
    public Need[] findNeeds(String query) throws IOException 
    {
        ArrayList<Need> newNeeds = new ArrayList<>(needs.values());

        for(Need need : newNeeds)
        {
            if(!need.getType().contains(query))
            {
                newNeeds.remove(need);
            }
        }
        
        return (Need[])newNeeds.toArray();
    }

    /*
     * Gets a Need by its ID
     * @param id The unique ID of the Need to get
     * @return The Need with the given ID, null if not found
     */
    @Override
    public Need getNeed(int id) throws IOException 
    {
        return needs.get(id);
    }

    @Override
    public Need createNeed(Need need) throws IOException 
    {
        //creating need Need because id is immutable
        Need newNeed = new Need(need.getCost(), need.getQuantity(), need.getType(), getNextID());
        //putting newNeed in the map of needs
        needs.put(newNeed.getId(), newNeed);
        return newNeed;
    }

    /*
     * Updates a Need based on the Need object
     * @param need The Need to update
     * @return The updated Need if successful, otherwise null
     */
    @Override
    public Need updateNeed(Need need) throws IOException 
    {
        //checking if need exists
        if(!needs.containsKey(need.getId()))
            return null;
        else
        {
            //otherwise update it
            needs.put(need.getId(), need);
            return need;
        }
    }

    /*
     * Deletes a Need based on its ID
     * @param id The unique ID of the Need to delete
     * @return The deleted Need if successful, otherwise null
     */
    @Override
    public Need deleteNeed(int id) throws IOException 
    {
        //checking if the need exists
        if(!needs.containsKey(id))
            return null;
        else
        {
            //otherwise remove it
            Need removeNeed = needs.get(id);
            needs.remove(id);
            return removeNeed;
        }
    }

    /*
     * Deletes a Need based on the Need object
     * @param need The Need to delete
     * @return The deleted Need if successful, otherwise null
     */
    @Override
    public Need deleteNeed(Need need) throws IOException 
    {
        //checking if the need exists
        if(!needs.containsKey(need.getId()))
            return null;
        else
        {
            //otherwise remove it
            needs.remove(need.getId());
            return need;
        }
    }
    
}
