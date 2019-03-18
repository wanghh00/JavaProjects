package hello;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {
	
	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	
	@RequestMapping(path="/greeting", method=RequestMethod.GET)
	public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
		try {
			if (name.equals("xxx"))
				Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}
	
	@RequestMapping(path="/search", method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> search(@RequestParam(value="q", defaultValue="Hello") String qstr) {
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("a", 123);
		ret.put("b", qstr);
		return ret;
	}
}
