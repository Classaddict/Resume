package Model.Decorator;

import Model.Comic;
import Model.ComicBookValue;

public abstract class ValueDecorator implements ComicBookValue{
    protected ComicBookValue value;

    public ValueDecorator(ComicBookValue value) {
        this.value = value;
    }
    
    public double totalValue() {
        return value.totalValue();
    }

    /**
     * Unwraps the decorator chain and returns the underlying Comic.
     * Needed so PersonalCollection can find and replace a comic by identity
     * even when it is wrapped in one or more decorators.
     */
    public Comic getWrapped() {
        if (value instanceof ValueDecorator vd) {
            return vd.getWrapped();   // recurse through chained decorators
        }
        return (Comic) value;         // base case: the raw Comic
    }
}
