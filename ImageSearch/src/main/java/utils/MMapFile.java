package utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class MMapFile implements AutoCloseable {
	static final Logger LOG = Logger.getLogger(MMapFile.class);
	public final static int EMBEDDING_SIZE = 4096;

	private File file;
	private RandomAccessFile fileput;
	private MappedByteBuffer buffer;
	private boolean mmapmode = false;
	private String mode = "r";

	public MMapFile(String path) {
		file = new File(path);
		// LOG.info("hehe " + file.length());
	}
	
	public MMapFile(String path, String mode) {
		file = new File(path);
		this.mode = mode;
	}

	public MMapFile open() {
		if (fileput != null) return this;
		
		try {
			fileput = new RandomAccessFile(file, mode);
		} catch (IOException e) {
			LOG.error("", e);
		}
		return this;
	}
	
	public File asFile() {
		return file;
	}
	
	public MappedByteBuffer asByteBuffer() {
		return buffer;
	}
	
	public RandomAccessFile asRandomAccessFile() {
		return fileput;
	}
	
	synchronized public MMapFile enableMmapMode() {
		if (buffer == null) {
			FileChannel fileChannel = fileput.getChannel();
			try {
				buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
				LOG.info("MMapMode enabled " + buffer);
			} catch (IOException e) {
				LOG.error("", e);
			}
		} else {
			LOG.info("MMapMode was enabled " + buffer);
		}
		mmapmode = buffer != null;
		return this;
	}
	
	public MMapFile load() {
		if (mmapmode == true && buffer.isLoaded() == false) {
			buffer.force();
			buffer.load();
			LOG.info("MMap loaded");
		}
		return this;
	}
	
	public long readLong() throws IOException {
		return mmapmode ? buffer.getLong() : fileput.readLong();
	}
	
	public int readInt() throws IOException {
		return mmapmode ? buffer.getInt() : fileput.readInt();
	}
	
	public void read(byte[] dst) throws IOException {
		if (mmapmode) buffer.get(dst);
		else fileput.read(dst);
	} 
	
	public void seek(long pos) throws IOException {
		if (mmapmode) buffer.position((int) pos);
		else fileput.seek(pos);
	}
	
	public long getFilePointer() throws IOException {
		return mmapmode ? buffer.position() : fileput.getFilePointer();
	}
	
	public void close() {
		try {
			if (buffer != null) {
				buffer.force();
				buffer = null;
			}
			fileput.close();
		} catch (IOException e) {
			LOG.error("", e);
		}
	}
}
