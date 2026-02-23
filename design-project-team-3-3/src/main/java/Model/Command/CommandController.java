package Model.Command;

import Model.Comic;
import Model.PersonalCollection;

public class CommandController {

    private PersonalCollection collection;
    private int userID;

    public CommandController(PersonalCollection collection, int userID) {
        this.collection = collection;
        this.userID = userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String addComic(Comic comic) {
        Action action = new AddComicCommand(collection,userID, comic);
        return action.performAction();
    }

    public String removeComic(Comic comic) {
        Action action = new RemoveComicCommand(collection,userID, comic);
        return action.performAction();
    }

    public String editComic(Comic oldComic, Comic newComic) {
        Action action = new EditComicCommand(collection, userID, oldComic, newComic);
        return action.performAction();
    }

    public String gradeComic(Comic comic, int grade) {
        Action action = new GradeComicCommand(collection, userID, comic, grade);
        return action.performAction();
    }

    public String slabComic(Comic comic) {
        Action action = new SlabComicCommand(collection, userID, comic);
        return action.performAction();
    }

    public String searchComic(String query) {
        Action action = new SearchComicCommand(collection, userID,query);
        return action.performAction();
    }

    /** Full control over exact/partial matching and sort order. */
    public String searchComic(String query, boolean exactMatch,
                               SearchComicCommand.SortOrder sortOrder) {
        return new SearchComicCommand(collection, userID, query, exactMatch, sortOrder)
                .performAction();
    }
}