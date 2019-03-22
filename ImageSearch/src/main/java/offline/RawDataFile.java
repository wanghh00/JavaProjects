package offline;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import utils.MMapFile;

public class RawDataFile implements AutoCloseable {
	static final Logger LOG = Logger.getLogger(RawDataFile.class);
	
	static String FILE_PATH = "/Users/hongwang/Downloads/cvswedb_%s_%s.bin";
	
	private MMapFile mapfile;
	private RandomAccessFile infile;
	private MappedByteBuffer buffer;
	private long filelength;
	private long idx = 0;
	
	private int EMBEDDING_SIZE = 4096;
	private int recordSize = 8 + 4 + EMBEDDING_SIZE;
	private byte[] embedding = new byte[EMBEDDING_SIZE];
	
	private Map<String, Object> record = new HashMap<String, Object>();
	
	public RawDataFile(String path) {
		mapfile = new MMapFile(path);
		infile = mapfile.open().asRandomAccessFile();
		
		try {
			filelength = infile.length();
		} catch (IOException e) {
			LOG.error("", e);
		}
	}
	
	public RawDataFile(int part, int category) {
		this(String.format(FILE_PATH, part, category));
	}
	
	public boolean hasNext() {
		try {
			return infile.getFilePointer() + recordSize <= filelength;
		} catch (IOException e) {
			LOG.error("", e);
			return false;
		}	
	}
	
	public Map<String, Object> next() {
		Map<String, Object> ret = readOne();
		ret.put("_id", idx);
		idx++;
		return ret;
	}
	
	public Map<String, Object> get(long idx) {
		Map<String, Object> ret = null;
		try {
			infile.seek(idx*recordSize);
			ret = readOne();
			ret.put("_id", idx);
		} catch (IOException e) {
			LOG.error("", e);
		}
		return ret;
	}
	
	public void close() {
		mapfile.close();
	}
	
	private Map<String, Object> readBufferOne() {
		record.clear();
		
		try {
			record.put("itemid", buffer.getLong());
			record.put("category", buffer.getInt());
			infile.read(embedding);
			record.put("embedding", embedding);
			
			return record;
		} catch (IOException e) {
			LOG.error("", e);
			return null;
		}
	}
	
	private Map<String, Object> readOne() {
		record.clear();
		
		try {
			record.put("itemid", infile.readLong());
			record.put("category", infile.readInt());
			infile.read(embedding);
			record.put("embedding", embedding);
			
			return record;
		} catch (IOException e) {
			LOG.error("", e);
			return null;
		}
	}
}
