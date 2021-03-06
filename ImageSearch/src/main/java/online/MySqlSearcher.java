package online;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mysql.cj.jdbc.MysqlDataSource;

import offline.MySqlIndexer;

public class MySqlSearcher {
	static final Logger LOG = Logger.getLogger(MySqlSearcher.class);
	
	public static Map<String, Object> getItemMeta(long itemId) {
		MysqlDataSource ds = MySqlIndexer.getMySQLDataSource();
		Map<String, Object> ret = null;
		ResultSet result = null;

		String query = "SELECT * FROM ItemEmbedding WHERE ItemId = ? LIMIT 1";
		try (Connection conn = ds.getConnection(); PreparedStatement preparedStmt = conn.prepareStatement(query)) {
			preparedStmt.setLong(1, itemId);
			
			result = preparedStmt.executeQuery();
			if (result.next()) {
				ret = new HashMap<String, Object>();
				ret.put("itemid", itemId);
				ret.put("part", result.getInt(2));
				ret.put("category", result.getInt(3));
				ret.put("offset", result.getLong(4));
			}
		} catch (SQLException ex) {
			LOG.error("", ex);
		} finally {
			try {
				result.close();
			} catch (SQLException e) {
				LOG.error("", e);
			}
		}
		return ret;
	}
}
