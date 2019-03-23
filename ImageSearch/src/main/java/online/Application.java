package online;

import java.util.concurrent.ForkJoinPool;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	static final Logger LOG = Logger.getLogger(Application.class);
	
	
	public static void main(String[] args) {
		LOG.info("HAHA " + ForkJoinPool.getCommonPoolParallelism());
		SpringApplication.run(Application.class, args);
	}

}
