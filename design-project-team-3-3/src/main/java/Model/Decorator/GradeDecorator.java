package Model.Decorator;
import java.lang.Math;
import Model.ComicBookValue;

public class GradeDecorator extends ValueDecorator {
    int grade;

    public GradeDecorator(ComicBookValue value, int grade) {
        super(value);
        this.grade = grade;
    }

    public int getGrade() {
        return this.grade;
    }

    @Override
    public double totalValue() {
        double val = super.value.totalValue();
        if (grade == 0) { // grade cannot be < 1
            return val;
        } else if (grade > 10) { // grade cannot be > 10
            return val;
        } else if (grade == 1) {
            return val * 0.10;
        } else { // grade is from 2-10
            return Math.log10(grade) * val;
        }
    }
}
