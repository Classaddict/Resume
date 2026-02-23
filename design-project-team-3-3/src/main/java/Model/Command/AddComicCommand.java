package Model.Command;

import Model.Comic;
import Model.PersonalCollection;
public class AddComicCommand implements Action{
    private PersonalCollection collection;
    private int userID;
    private Comic comic;
    public AddComicCommand(PersonalCollection c, int userID, Comic com) {
        this.collection = c;
        this.userID = userID;
        this.comic=com;
    }
    @Override
    public String performAction() {
        collection.addComic(comic);
        return comic.toString();
    }
}