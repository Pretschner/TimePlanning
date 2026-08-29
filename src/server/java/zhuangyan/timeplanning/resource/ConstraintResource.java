package zhuangyan.timeplanning.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhuangyan.timeplanning.model.GroupConstraint;
import zhuangyan.timeplanning.service.AuthenticationService;
import zhuangyan.timeplanning.service.ConstraintService;

import java.util.List;

@RestController
public class ConstraintResource {

    private final ConstraintService constraintService;
    private final AuthenticationService authenticationService;

    @Autowired
    public ConstraintResource(ConstraintService constraintService, AuthenticationService authenticationService) {
        this.constraintService = constraintService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/constraints")
    public ResponseEntity<GroupConstraint> createGroupConstraint(@RequestBody GroupConstraint constraint, @RequestHeader("Authorization") String authHeader) {
        if (constraint.id() != null && constraint.id() > 0) {
            return ResponseEntity.badRequest().build();
        }
        long userId = authenticationService.getUserId(authHeader);
        GroupConstraint res = constraintService.createGroupConstraint(constraint, userId);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/constraints/{id}")
    public ResponseEntity<GroupConstraint> updateGroupConstraint(@RequestBody GroupConstraint constraint, @PathVariable long id, @RequestHeader("Authorization") String authHeader) {
        if (constraint.id() != id) {
            return ResponseEntity.badRequest().build();
        }
        long userId = authenticationService.getUserId(authHeader);
        GroupConstraint res = constraintService.updateGroupConstraint(constraint, id, userId);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/constraints/{id}")
    public ResponseEntity<GroupConstraint> deleteGroupConstraint(@PathVariable long id, @RequestHeader("Authorization") String authHeader) {
        if (id <= 0) {
            return ResponseEntity.badRequest().build();
        }
        long userId = authenticationService.getUserId(authHeader);
        GroupConstraint deletedTask = constraintService.deleteGroupConstraint(id, userId);
        return ResponseEntity.ok(deletedTask);
    }

    @GetMapping("/constraints")
    public ResponseEntity<List<GroupConstraint>> getGroupConstraints(@RequestHeader("Authorization") String authHeader) {
        long userId = authenticationService.getUserId(authHeader);
        return ResponseEntity.ok(constraintService.getGroupConstraints(userId));
    }
}
