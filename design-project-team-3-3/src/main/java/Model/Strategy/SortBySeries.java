package Model.Strategy;


import Model.Comic;

public class SortBySeries implements ComicSorter {

    /**
     * @param c1
     * @param c2
     * The Concrete Strategy that sorts a collection based on a comic's series title. 
     * If the difference is the same, then their volume numbers are compared. 
     */
    @Override
    public int compare(Comic c1, Comic c2) {
        int series_title = c1.getSeriesTitle().compareTo(c2.getSeriesTitle());
        if (series_title != 0) {
            return series_title;
        }

        return Integer.compare(c1.getVolume(), c2.getVolume());

    }

}
