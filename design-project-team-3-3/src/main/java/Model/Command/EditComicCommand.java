package Model.Command;

import Model.Comic;
import Model.PersonalCollection;

public class EditComicCommand implements Action{
    private PersonalCollection collection;
    private int userID;
    private Comic old_comic;
    private Comic new_comic;
    public EditComicCommand(PersonalCollection c, int userID, Comic o_com,Comic n_com) {
        this.collection = c;
        this.userID = userID;
        this.old_comic=o_com;
        this.new_comic=n_com;
    }
    @Override
    public String performAction() {
        collection.editComic(old_comic,new_comic);
        return "Comic edited sucessfully";
    }
}
