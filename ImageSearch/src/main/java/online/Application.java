package online;

import java.util.concurrent.ForkJoinPool;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	static final Logger LOG = Logger.getLogger(Application.class);

	public static void main(String[] args) {
		LOG.info("ForJoinPool Thread Num: " + ForkJoinPool.getCommonPoolParallelism());

		ItemSearchScheduler scheduler = new ItemSearchScheduler();
		Controller.setItemSearchScheduler(scheduler);

		SpringApplication.run(Application.class, args);

	}
}
