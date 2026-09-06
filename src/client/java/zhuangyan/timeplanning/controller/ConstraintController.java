package zhuangyan.timeplanning.controller;

import zhuangyan.timeplanning.model.GroupConstraint;

public class ConstraintController extends CrudController<GroupConstraint> {
    public ConstraintController() {
        super("constraints", GroupConstraint.class, GroupConstraint::id);
    }
}