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
			//FileChannel fileChannel = fileput.getChannel();
			//buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
			
			//LOG.info("haha " + fileChannel.size());
		} catch (IOException e) {
			LOG.error("", e);
		}
		return this;
	}
	
	public MappedByteBuffer asByteBuffer() {
		FileChannel fileChannel = fileput.getChannel();
		try {
			buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
		} catch (IOException e) {
			LOG.error("", e);
		}
		return buffer;
	}
	
	public RandomAccessFile asRandomAccessFile() {
		return fileput;
	}
	
	public void close() {
		try {
			buffer = null;
			fileput.close();
		} catch (IOException e) {
			LOG.error("", e);
		}
	}
	
	public static void walkThroughBinFile(String path) {
		MMapFile mapfile = new MMapFile(path);
		RandomAccessFile infile = mapfile.open().asRandomAccessFile();

		try {
			int x = 0;
			while (true) {
				long id = infile.readLong();
				int category = infile.readInt();
				infile.skipBytes(EMBEDDING_SIZE);
				x++;

				if (x % 1000 == 0) {
					LOG.info(String.format("%s %s %s", x, id, category));
					LOG.info(String.format("File Info: %s %s", infile.getFilePointer(), infile.length()));
					//infile.getFilePointer();
				}

			}
		} catch (IOException e) {
			LOG.error("", e);
		}
		mapfile.close();
	}
	
	public static void main(String[] args) {
		String path = "/Users/hongwang/Downloads/cvswedb_0_2.bin";
		walkThroughBinFile(path);
	}
	
	public static void main1(String[] args) {
		String path = "/Users/hongwang/Downloads/cvswedb_2";
		MMapFile mapfile = new MMapFile(path+".bin");
		RandomAccessFile infile = mapfile.open().asRandomAccessFile();
		byte[] embeding = new byte[EMBEDDING_SIZE];
		
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
