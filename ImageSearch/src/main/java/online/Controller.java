package online;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
	static final Logger LOG = Logger.getLogger(Controller.class);
	
	@PostMapping("/imgsch")
	@ResponseBody
	public Map<String, Object> search(@RequestBody Map<String, Object> requst) {
		LOG.info(requst);
		
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("a", 123);
		ret.put("b", "hehe");
		return ret;
	}
}
