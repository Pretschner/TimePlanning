package zhuangyan.timeplanning.controller;

import zhuangyan.timeplanning.model.GroupConstraint;

/** Tiny subclass pointing {@code CrudController} at {@code /constraints} with {@code GroupConstraint}/{@code Long}. */
public class ConstraintController extends CrudController<GroupConstraint> {
    public ConstraintController() {
        super("constraints", GroupConstraint.class, GroupConstraint::id);
    }
}