package com.ufund.api.ufundapi.Controller;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.ufund.api.ufundapi.Need.Need;
import com.ufund.api.ufundapi.Need_Cupboard.Need_Cupboard;
import com.ufund.api.ufundapi.Persistance.NeedDAO;

public class NeedController 
{
        private NeedDAO needDao;

        /*
         * Lists all Needs in the cupboard
         * @return A ResponseEntity containing an array of all Needs and an HTTP status code
         *         or an error status code if something goes wrong
         */
        public ResponseEntity<Need[]> ListNeed(){
            LOG.info("GET /needs");
            try{
                Need[] listOfNeeds = needDao.getNeeds();
                return new ResponseEntity<Need[]>(listOfNeeds, HttpStatus.OK);
            }
            catch(IOException e){
                LOG.log(Level.SEVERE,e.getLocalizedMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        public ResponseEntity<List<Need>> searchNeed(@RequestParam String name,Need_Cupboard cupboard) 
        {
        List<Need> results = new ArrayList<>();
        Need[] allNeeds;
        allNeeds = cupboard.getNeeds();
        if (allNeeds != null) {
            for (Need n : allNeeds) {
                if (n.getType().equalsIgnoreCase(name)) {
                    results.add(n);
                }
            }
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (results.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    private static final Logger LOG = Logger.getLogger(NeedController.class.getName());

    public ResponseEntity<Need> deleteNeed(int id){
            //traveres through the cupboard to find the need 
            try {
                Need[] needs =needDao.getNeeds();
                for(int i=0;i<needs.length;i++){
                    if(needs[i].getId()==id){
                        //if it finds the need, sets it to null
                        needDao.deleteNeed(id);
                        //returns ok
                        return new ResponseEntity<>(HttpStatus.OK);
                    }
                }
                //need was not found in the cupboard, returns not found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (IOException e) {
                //catches any io exceptions and returns internal server error.
                LOG.severe(e.getLocalizedMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
               
    }

    /*
     * Edits a Need in the given Need_Cupboard
     * @param need The Need to edit
     * @param changeProperty The property to change (String for type, Integer for quantity)
     * @return A ResponseEntity with an HTTP status code indicating success or failure
     */
    public ResponseEntity<Need> editNeed(Need need, String type){
            try {
                Need[] needs = needDao.getNeeds();
                need.setType(type);
                //traveres through the cupboard to find the need 
                for(int i=0;i<needs.length;i++){
                    if(needs[i].equals(need)){
                        //if it finds the need, sets it to null
                        needs[i]=need;
                        needDao.updateNeed(need);
                        //returns ok
                        return new ResponseEntity<>(HttpStatus.OK);
                    }
                }
                //need was not found in the cupboard, returns not found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            } catch (IOException e) {
                //catches any io exceptions and returns internal server error.
                LOG.severe(e.getLocalizedMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }  
    }
    public ResponseEntity<Need> editNeed(Need need, int quantity){
            try {
                Need[] needs = needDao.getNeeds();
                need.setQuantity(quantity);
                //traveres through the cupboard to find the need 
                for(int i=0;i<needs.length;i++){
                    if(needs[i].equals(need)){
                        //if it finds the need, sets it to null
                        needs[i]=need;
                        needDao.updateNeed(need);
                        //returns ok
                        return new ResponseEntity<>(HttpStatus.OK);
                    }
                }
                //need was not found in the cupboard, returns not found
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); 
            } catch (IOException e) {
                //catches any io exceptions and returns internal server error.
                LOG.severe(e.getLocalizedMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }  
    }

    @GetMapping("/{id}")
    public ResponseEntity<Need> getNeed(@PathVariable int id) {
        LOG.info("GET /Need/" + id);
        try {
            Need need = needDao.getNeed(id);
            if (need != null)
                return new ResponseEntity<Need>(need,HttpStatus.OK);
            else
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        catch(IOException e) {
            LOG.log(Level.SEVERE,e.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
