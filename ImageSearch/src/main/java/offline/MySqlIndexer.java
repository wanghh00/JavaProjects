package offline;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.mysql.cj.jdbc.MysqlDataSource;

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
				
		ds.setURL(props.getProperty("db.url"));
		ds.setUser(props.getProperty("db.user"));
		ds.setPassword(props.getProperty("db.passwd"));

		return ds;
	}
	
	
	public static void indexRawDataEmbedding(int part, int category) {
		MysqlDataSource ds = getMySQLDataSource();
		String insert = "INSERT INTO ItemEmbedding (ItemId, Part, Category, Offset)" + " VALUES (?, ?, ?, ?)";

		try (Connection conn = ds.getConnection(); RawDataFile datafile = new RawDataFile(part, category);
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
			LOG.info(String.format("Total: %s for part %s", idx, part));
		} catch (SQLException ex) {
			LOG.error("", ex);
		}
	}
	
	public static void main2(String[] args) {
		// String path = "/Users/hongwang/Downloads/cvswedb_0_2.bin";
		// walkThroughBinFile(path);
		//indexRawDataEmbedding(path, 0);
		
		// "cvswedb_1_0.bin" Dup Pri Key?
		
		// String[] paths = {"cvswedb_2_0.bin", "cvswedb_2_1.bin", "cvswedb_2_2.bin", "cvswedb_2_3.bin"};
		for (int part = 0; part < 3; part++) {
			for (int cate = 0; cate < 4; cate++)
			indexRawDataEmbedding(part, cate);
		}
	}
}
