package com.ufund.api.ufundapi.Persistance;

import java.io.IOError;
import java.io.IOException;

import com.ufund.api.ufundapi.Need.*;

/**
 * Interface for the persistance of {@linkplain Need Need} objects
 * (Basically it just keeps them all stored in one tidy place)
 * 
 * @author Bobby Orbin
 */

 public interface NeedDAO
 {

    /**
     * Gets all {@linkplain Need Need} objects
     * 
     * @return A native Array of {@linkplain Need Need} objects
     */
    Need[] getNeeds() throws IOException;

    /**
     * Searches needs that contain query and returns them
     * 
     * @param query The search query for {@linkplain Need Needs}
     * 
     * @return A native array of {@linkplain Need Needs} that contain the query
     * 
     * @throws IOException if an issue with underlying storage
     */
    Need[] findNeeds(String query) throws IOException;


    /**
     * gets a {@linkplain Need Need} with the specified id
     * 
     * @param id the unique ID of the {@linkplain Need need}
     * 
     * @return the found {@linkplain Need Need}
     */
    Need getNeed(int id) throws IOException;


    /**
     * Creates and stores a {@linkplain Need Need}
     * 
     * @param need The {@linkplain Need Need} to be created and stored
     * 
     * @return The {@linkplain Need Need} if succesful, otherwise null
     */
    Need createNeed(Need need) throws IOException;

    /**
     * updates and saves a {@linkplain Need Need} 
     * 
     * @param need The {@link Need Need} to be updated
     * 
     * @return the updated {@linkplain Need Need} if sucessful, otherwise null
     */
    Need updateNeed(Need need)throws IOException;

    /**
     * Deletes a {@linkplain Need Need} with the given id(works similar to pop)
     * 
     * @param id The unique ID of the {@linkplain Need Need} to be deleted
     * 
     * @return the need deleted if sucessful, otherwise null
     */
    Need deleteNeed(int id)throws IOException;

   /**
     * Deletes the need supplied (works similar to pop)
     * 
     * @param need The {@linkplain Need Need} to be deleted
     * 
     * @return the need deleted if sucessful, otherwise null
     */
    Need deleteNeed(Need need)throws IOException;





 }