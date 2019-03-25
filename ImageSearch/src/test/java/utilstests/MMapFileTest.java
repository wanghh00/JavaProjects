package utilstests;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import utils.MMapFile;

public class MMapFileTest {
	static final Logger LOG = Logger.getLogger(MMapFileTest.class);
	
	@Test
	public void asRandomAccessFile() {
		String path = "/Users/hongwang/Downloads/cvswedb_2";
		MMapFile mapfile = new MMapFile(path+".bin");
		RandomAccessFile infile = mapfile.open().asRandomAccessFile();
		byte[] embeding = new byte[MMapFile.EMBEDDING_SIZE];
		
		Map<Integer, RandomAccessFile> mapFile = new HashMap<Integer, RandomAccessFile>();
		for (int x = 0; x<4; x++) {
			MMapFile newfile = new MMapFile(path + "_" + x + ".bin", "rw");
			mapFile.put(x, newfile.open().asRandomAccessFile());
		}
		
		try {
			//file.wr
			int x = 0;
			while (true) {
				//LOG.info(String.format(" %s %s", file.readLong(), file.readInt()));
				long id = infile.readLong();
				int category = infile.readInt();
				infile.read(embeding);
				
				RandomAccessFile outfile = mapFile.get(category);
				outfile.writeLong(id);
				outfile.writeInt(category);
				outfile.write(embeding);
				
				x++;
				if (x % 1000 == 0) {
					LOG.info(String.format("%s %s %s", x, id, category));
				}
			}
		} catch (IOException e) {
			LOG.error("", e);
		}
		
		for (RandomAccessFile file : mapFile.values()) {
			try {
				file.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		mapfile.close();
	}

}
