package Model.Command;

import Model.Comic;
import Model.PersonalCollection;

public class SlabComicCommand implements Action{
    private PersonalCollection collection;
    private int userID;
    private Comic comic;

    public SlabComicCommand(PersonalCollection c, int userID, Comic com) {
        this.collection = c;
        this.userID = userID;
        this.comic=com;
    }

    @Override
    public String performAction() {
        if (!comic.isGraded()) {
            return "Cannot slab '" + comic.getSeriesTitle() + "' — comic must be graded first.";
        }
        if (comic.isSlabbed()) {
            return "'" + comic.getSeriesTitle() + "' is already slabbed.";
        }

        double oldValue = comic.getValue();
        double newValue = oldValue * 2.0;

        comic.setValue(newValue);
        comic.setSlabbed(true);

        return comic.toString();
    }
}
