package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

public class FileUtils {
	static final Logger LOG = Logger.getLogger(FileUtils.class);

	public static MappedByteBuffer mmapFile4Read(String path) {
		RandomAccessFile file = null;
		MappedByteBuffer buffer = null;
		try {
			file = new RandomAccessFile(new File(path), "r");
			FileChannel fileChannel = file.getChannel();
			buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

		} catch (IOException e) {
			LOG.error("", e);
		}
		return buffer;
	}

	public static void main(String[] args) {
		// String path = "/Users/hongwang/Downloads/cvswedb_0.bin";
		String path = "/Users/hongwang/Downloads/cvswedb_0.bin";

		File file = new File("/Users/hongwang/Downloads/cvswedb_0.bin");
		LOG.info(file.length());
	}

}
