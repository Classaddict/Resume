package Model.Strategy;

import java.util.List;

import Model.Comic;
import Model.PersonalCollection;

/**
 * The class that represents the Context Class in the Strategy pattern
 */
public class CollectionList {
    private ComicSorter sorter;
    private PersonalCollection collection;

    public CollectionList(PersonalCollection collection) {
        this.collection = collection;
        this.sorter     = new SortByName(); // sensible default
    }
    
    public void setSorter(ComicSorter sorter) {
        this.sorter = sorter;
        collection.setStrategy(sorter);
        sort();
    }

    public void sort() {
        List<Comic> comics = collection.getComics(); // composite
        comics.sort(this.sorter);
    }

    /** Returns a sorted snapshot for the PTUI to display. 
     * TEMPORARY FOR DEBUGGING
    */
    public List<Comic> getSortedComics() {
        List<Comic> comics = collection.getComics();
        comics.sort(this.sorter);
        return comics;
    }
}  
