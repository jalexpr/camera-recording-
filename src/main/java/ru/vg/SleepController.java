package ru.vg;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController()
@RequestMapping("/api")
public class SleepController {
    @RequestMapping(value = "sleep", method = RequestMethod.GET)
    public void sleep(@RequestParam Map<String, String> params) throws IOException {
        Process p = Runtime.getRuntime().exec("cmd /c start \"\" sleep.bat");
    }
}
