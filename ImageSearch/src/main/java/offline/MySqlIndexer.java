package offline;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;

import org.apache.log4j.Logger;

import com.mysql.cj.jdbc.MysqlDataSource;

import online.ItemComparator;
import online.ItemSearchTask;
import utils.ItemFeature;
import utils.MMapFile;

public class MySqlIndexer {
	static final Logger LOG = Logger.getLogger(MySqlIndexer.class);
	
	private static MysqlDataSource MYSQL_DS;
	
	public static MysqlDataSource getMySQLDataSource() {
		if (MYSQL_DS == null) {
			synchronized (MySqlIndexer.class) {
				if (MYSQL_DS == null) {
					MYSQL_DS = _getMySQLDataSource();
				}
			}
		}
		return MYSQL_DS;
	}

	private static MysqlDataSource _getMySQLDataSource() {
		Properties props = new Properties();
		String fileName = "src/main/resources/db.properties";

		try (FileInputStream fis = new FileInputStream(fileName)) {
			props.load(fis);
		} catch (IOException ex) {
			LOG.error("", ex);
		}

		MysqlDataSource ds = new MysqlDataSource();
		
		LOG.info(props.getProperty("db.user"));
		
		ds.setURL(props.getProperty("db.url"));
		ds.setUser(props.getProperty("db.user"));
		ds.setPassword(props.getProperty("db.passwd"));

		return ds;
	}
	
	public static void walkThroughBinFile(String path) {
		RawDataFile datafile = new RawDataFile(path);
		datafile.enableMmapMode();
		
		ItemFeature feature = new ItemFeature();
		
		Set<Long> set = new HashSet<Long>();
		int dupnum = 0;
		
		ItemFeature src = new ItemFeature();
		datafile.getItemFeature(10, src);
		
		ItemComparator.Compare comp = new ItemComparator.HammingDist();
		
		long start = System.currentTimeMillis();
		int idx = 0;
		
		while (datafile.hasNext()) {
			datafile.nextItemFeature(feature);
			float sim = comp.similarity(src.embedding, feature.embedding);
			idx++;
			//Map<String, Object> one = datafile.next();
			
//			long idx = (long) one.get("_id");
//			Long itemid = (Long) one.get("itemid");
//			
//			boolean notexist = set.add(itemid);
//			if (notexist == false) {
//				//LOG.info("HAHA " + itemid);
//				dupnum++;
//			}
//			
			//LOG.info(idx);
			if (idx % 1000 == 0) {
				// LOG.info(String.format("%s %s %s", idx, one.get("itemid"), one.get("category")));
				LOG.info("Sim: " + sim);
			}
		}
		LOG.info("Running time: " + (System.currentTimeMillis() - start));
		LOG.info("DupNum: " + dupnum);
		
		datafile.close();
	}
	
	public static float doItemSearchTask(ItemSearchTask task) {
		long start = System.currentTimeMillis();
		ItemFeature feature = new ItemFeature();
		float sim = 0.0f;
		
		while (task.datafile.hasNext()) {
			task.datafile.nextItemFeature(feature);
			sim = task.comp.similarity(task.src.embedding, feature.embedding);
		}
		LOG.info(Thread.currentThread().getName() + " Running time: " + (System.currentTimeMillis() - start));
		return sim;
	}
	
	public static void searchInParallel() {
		String path = "/Users/hongwang/Downloads/cvswedb_0_0.bin";
		String path1 = "/Users/hongwang/Downloads/cvswedb_1_0.bin";
		String path2 = "/Users/hongwang/Downloads/cvswedb_2_0.bin";
		
		RawDataFile datafile = new RawDataFile(path);
		datafile.enableMmapMode();
		RawDataFile datafile1 = new RawDataFile(path1);
		datafile1.enableMmapMode();
		RawDataFile datafile2 = new RawDataFile(path2);
		datafile2.enableMmapMode();
		
		ItemFeature src = new ItemFeature();
		datafile.getItemFeature(10, src);
		
		List<ItemSearchTask> searchTasks = new ArrayList<ItemSearchTask>();
		searchTasks.add(new ItemSearchTask(datafile, src, new ItemComparator.HammingDist(), 100));
		searchTasks.add(new ItemSearchTask(datafile1, src, new ItemComparator.HammingDist(), 100));
		searchTasks.add(new ItemSearchTask(datafile2, src, new ItemComparator.HammingDist(), 100));
		
		searchTasks.parallelStream().forEach(task -> doItemSearchTask(task));
		
		// ForkJoinPool threadPool = new ForkJoinPool(4);
	}
	
	public static void main(String[] args) {
		LOG.info(ForkJoinPool.getCommonPoolParallelism());
		searchInParallel();
	}
	
	public static void main3(String[] args) {
		String path = "/Users/hongwang/Downloads/cvswedb_1_0.bin";
		walkThroughBinFile(path);
	}
	
	public static void main2(String[] args) {
		// String path = "/Users/hongwang/Downloads/cvswedb_0_2.bin";
		// walkThroughBinFile(path);
		//indexRawDataEmbedding(path, 0);
		
		// "cvswedb_1_0.bin" Dup Pri Key?
		
		// String[] paths = {"cvswedb_2_0.bin", "cvswedb_2_1.bin", "cvswedb_2_2.bin", "cvswedb_2_3.bin"};
		String[] paths = {"cvswedb_1_0.bin"};
		for (String one : paths) {
			String dir = "/Users/hongwang/Downloads/" + one;
			indexRawDataEmbedding(dir, 1);
		}
	}
	
	public static void indexRawDataEmbedding(String path, int part) {
		MysqlDataSource ds = getMySQLDataSource();
		String insert = "INSERT INTO ItemEmbedding (ItemId, Part, Category, Offset)" + " VALUES (?, ?, ?, ?)";

		try (Connection conn = ds.getConnection(); RawDataFile datafile = new RawDataFile(path);
				PreparedStatement preparedStmt = conn.prepareStatement(insert)) {

			long idx = 0;
			while (datafile.hasNext()) {
				Map<String, Object> one = datafile.next();
				idx = (long) one.get("_id");
				long itemId = (long) one.get("itemid");
				
				if (itemId == 0) continue;

				// create the mysql insert preparedstatement
				preparedStmt.setLong(1, itemId);
				preparedStmt.setInt(2, part);
				preparedStmt.setInt(3, (int) one.get("category"));
				preparedStmt.setLong(4, idx);

				// execute the preparedstatement
				preparedStmt.addBatch();
				
				if (idx % 1000 == 0) {
					preparedStmt.executeBatch();
					LOG.info(String.format("%s %s %s", idx, one.get("itemid"), one.get("category")));
				}
			}
			preparedStmt.executeBatch();
			LOG.info(String.format("Total: %s for %s", idx, path));

		} catch (SQLException ex) {
			LOG.error("", ex);
		}

	}

	public static void main1(String[] args) {

		MysqlDataSource ds = getMySQLDataSource();

		String query = "SELECT VERSION()";
		String insert = "INSERT INTO ItemEmbedding (ItemId, Parition, Category, Offset)"
				        + " VALUES (?, ?, ?, ?)";

		try (Connection con = ds.getConnection();
				PreparedStatement pst = con.prepareStatement(query);
				ResultSet rs = pst.executeQuery()) {

			if (rs.next()) {
				String version = rs.getString(1);
				System.out.println(version);
			}
			
			
		} catch (SQLException ex) {
			LOG.error("", ex);
		}
	}
}
