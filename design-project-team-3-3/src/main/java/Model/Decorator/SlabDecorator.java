package Model.Decorator;

import Model.ComicBookValue;

public class SlabDecorator extends ValueDecorator {
    public SlabDecorator(ComicBookValue value) {
        super(value);
        if (!(value instanceof GradeDecorator)) {
            throw new IllegalStateException(
                "A comic cannot be slabbed unless it is graded first.");
        }
    }

    @Override
    public double totalValue() {
        double value = (super.value.totalValue() * 2);
        return value;
    }

    public void slabComic() {
        //
    }

}
