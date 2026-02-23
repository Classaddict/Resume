package Model.Strategy;


import Model.Comic;

public class SortByNumber implements ComicSorter {

    /**
     * @param c1
     * @param c2
     * A Concrete Strategy that has the sorting strategy of initially comparing the values of two books. 
     * If they are equivalent, then the issue number is then compared. 
     * If that is also equal, then the final criteria would be the issue count to be compared. 
     */
    @Override
    public int compare(Comic c1, Comic c2) {
        int value_diff = Double.compare(c1.getValue(), c2.getValue());
        if (value_diff != 0) {
            return value_diff;
        }

        int issue_diff = Integer.compare(c1.getIssue(), c2.getIssue());
        if (issue_diff != 0) {
            return issue_diff;
        }

        return Integer.compare(c1.getIssueCount(), c2.getIssueCount());
        
    }

    
    
}
