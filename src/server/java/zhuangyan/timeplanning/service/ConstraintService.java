package zhuangyan.timeplanning.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.exception.ForbiddenException;
import zhuangyan.timeplanning.exception.NotFoundException;
import zhuangyan.timeplanning.repository.ConstraintRepository;

import java.util.List;

@Service
public class ConstraintService {

    private final ConstraintRepository constraintRepository;

    @Autowired
    public ConstraintService(ConstraintRepository constraintRepository) {
        this.constraintRepository = constraintRepository;
    }

    public GroupConstraint createGroupConstraint(GroupConstraint constraint, long userId) {
        GroupConstraint newGroupConstraint = new GroupConstraint(constraintRepository.getNextId(), constraint.sourceGroup(), constraint.targetGroup(), constraint.minimumGap(), constraint.maximumGap());
        return constraintRepository.save(newGroupConstraint, userId);
    }

    public GroupConstraint updateGroupConstraint(GroupConstraint constraint, long id, long userId) {
        validateOwnership(id, userId);
        GroupConstraint updatedGroupConstraint = new GroupConstraint(id, constraint.sourceGroup(), constraint.targetGroup(), constraint.minimumGap(), constraint.maximumGap());
        return constraintRepository.save(updatedGroupConstraint, userId);
    }

    public GroupConstraint deleteGroupConstraint(long id, long userId) {
        validateOwnership(id, userId);
        return constraintRepository.deleteById(id);
    }

    private void validateOwnership(long taskId, long userId) {
        long ownerId = constraintRepository.getOwnerId(taskId)
                .orElseThrow(() -> new NotFoundException("The constraint queried does not exist."));
        if (ownerId != userId) {
            throw new ForbiddenException("The constraint queried does not belong to the user.");
        }
    }

    public List<GroupConstraint> getGroupConstraints(long userId) {
        return constraintRepository.findByUserId(userId);
    }
}
