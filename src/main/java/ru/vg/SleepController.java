package ru.vg;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController()
@RequestMapping("/api")
public class SleepController {
    @RequestMapping(value = "sleep", method = RequestMethod.GET)
    public String sleep() throws IOException {
        return call("sleep.bat");
    }

    @RequestMapping(value = "restart", method = RequestMethod.GET)
    public String  restart() throws IOException {
        return call("restart.bat");
    }

    private String call(String bat) throws IOException {
        Process p = Runtime.getRuntime().exec("cmd /c start \"\" " + bat);
        if (p.getErrorStream() != null) {
            return new BufferedReader(new InputStreamReader(p.getErrorStream(), "cp866")).readLine();
        } else {
            return "ok";
        }
    }
}
