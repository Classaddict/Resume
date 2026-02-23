package Model.Command;

import Model.Comic;
import Model.Decorator.GradeDecorator;
import Model.PersonalCollection;

public class GradeComicCommand implements Action{
    private PersonalCollection collection;
    private int userID;
    private Comic comic;
    private int grade;

    public GradeComicCommand(PersonalCollection c, int userID, Comic com, int grade) {
        this.collection = c;
        this.userID = userID;
        this.comic = com;
        this.grade = grade;
    }

    @Override
    public String performAction() {
        GradeDecorator graded = new GradeDecorator(comic, grade); // applies decorator to this command
        double newValue = graded.totalValue();
        comic.setValue(newValue);
        comic.setGraded(true);
        return comic.toString();
    }
}
