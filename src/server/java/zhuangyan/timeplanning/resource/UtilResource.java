package zhuangyan.timeplanning.resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UtilResource {

    @GetMapping("/help")
    public String getDocumentation() {
        return "help.html";
    }
}
