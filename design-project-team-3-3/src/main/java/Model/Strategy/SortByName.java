package Model.Strategy;


import Model.Comic;

public class SortByName implements ComicSorter {

    /**
     * @param c1 
     * @param c2
     * A Concrete Strategy that first sorts a comic based on its series title.
     * If the series titles have a match, then the comic's creators are then created to establish an order
     */
    @Override
    public int compare(Comic c1, Comic c2) {
        int title_diff = c1.getSeriesTitle().compareToIgnoreCase(c2.getSeriesTitle());
        if (title_diff != 0) {
            return title_diff;
        }

        return c1.getCreators().compareToIgnoreCase(c2.getCreators()); // may change
    }

}
