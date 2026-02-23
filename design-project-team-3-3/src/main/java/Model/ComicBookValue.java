package Model;
/**
 * Component interface for the Decorator pattern used to dynamically
 * adjust the value of a comic book.
 *
 * The three concrete implementations in this hierarchy are:
 *   - {@link Comic}          – Concrete Component (base, unmodified value)
 *   - {@code GradeDecorator} – Concrete Decorator (applies grading formula)
 *   - {@code SlabDecorator}  – Concrete Decorator (doubles the graded value)
 *
 * Grading formula:
 *   grade 1       → value × 0.10
 *   grade 2–10    → log₁₀(grade) × value
 *
 * Slabbing (only allowed when graded):
 *   slabbed value → graded value × 2
 */
public interface ComicBookValue {

    /**
     * Returns the total calculated value of the comic, taking into account
     * any applied decorators (grading, slabbing).
     *
     * @return the effective monetary value of this comic
     */
    double totalValue();
}
