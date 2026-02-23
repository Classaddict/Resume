package Model.Command;

import Model.Comic;
import Model.PersonalCollection;

public class RemoveComicCommand implements Action{
    private PersonalCollection collection;
    private int userID;
    private Comic comic;
    public RemoveComicCommand(PersonalCollection c, int userID, Comic com) {
        this.collection = c;
        this.userID = userID;
        this.comic=com;
    }
    @Override
    public String performAction() {
        boolean result=collection.removeComic(comic);
        if(result==true){
            return "Comic removed successfully";
        }
        return "Comic could not be removed";
    }
}
