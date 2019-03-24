package online;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import utils.ItemFeature;

@RestController
public class Controller {
	static final Logger LOG = Logger.getLogger(Controller.class);
	
	static final String IMG_URL_TEMPLATE = "http://37fee.http.sjc01.cdn.softlayer.net/cvswe/img/%s.jpg";

	private static ItemSearchScheduler Scheduler;

	public static void setItemSearchScheduler(ItemSearchScheduler sched) {
		Scheduler = sched;
	}

	@PostMapping("/imgsch")
	@ResponseBody
	public Map<String, Object> search(@RequestBody Map<String, Object> requst) {
		long start = System.currentTimeMillis();
		
		LOG.info(requst);
		Map<String, Object> ret = new HashMap<String, Object>();

		try {
			long itemId = (long) requst.get("itemId");
			int category = (int) requst.get("category");
			int numOfResult = (int) requst.get("numOfResults");

			ItemFeature feature = Scheduler.getItemFeature(itemId);
			feature.numOfResults = numOfResult;
			feature.category = category;

			List<ItemSearchTask> searchTasks = Scheduler.genSearchTasks(feature);
			Optional<PriorityQueue<ItemSearchResult>> results = searchTasks.parallelStream()
					.map(task -> ItemSearchScheduler.mapItemSearchTask(task))
					.reduce((ret1, ret2) -> ItemSearchScheduler.reduceItemSearchTask(ret1, ret2));
			
			Object[] outlist = results.get().toArray();
			Arrays.sort(outlist);
			
			List<Map<String, Object>> resultLst = new ArrayList<Map<String, Object>>();
			for (int x = outlist.length - 1; x >= 0; x--) {
				Map<String, Object> obj = new HashMap<String, Object>();
				ItemSearchResult one = (ItemSearchResult) outlist[x];
				
				obj.put("itemId", one.itemId);
				obj.put("score", one.sim);
				obj.put("imageUrl", String.format(IMG_URL_TEMPLATE, one.itemId));
				resultLst.add(obj);
			}
			
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("itemId", itemId);
			item.put("category", category);
			item.put("imageUrl", String.format(IMG_URL_TEMPLATE, itemId));
			
			ret.put("item", item);
			ret.put("results", resultLst);
			ret.put("time", System.currentTimeMillis() - start);
		} catch (RuntimeException e) {
			LOG.error("", e);
			ret.clear();
			ret.put("error", e);
		}
		return ret;
	}
}
