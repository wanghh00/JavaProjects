package offline;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import online.ItemFeature;
import utils.MMapFile;

public class RawDataConverter {
	static final Logger LOG = Logger.getLogger(RawDataConverter.class);
	
	
	public static class InputConfig implements AutoCloseable {
		static final String PATH = "/Users/hongwang/Downloads/cvswedb_%s.bin";
		public RawDataFile datafile;
		
		public InputConfig(int part) {
			datafile = new RawDataFile(String.format(PATH, part));
		}

		@Override
		public void close() throws Exception {
			datafile.close();
		}
	}
	
	public static class OutputConfig implements AutoCloseable {
		static final String PATH = "/tmp/cvswedb_%s_%s.bin";
		
		private Map<Integer, RandomAccessFile> mapFile = new HashMap<Integer, RandomAccessFile>();
		
		@SuppressWarnings("resource")
		public OutputConfig(int part) {
			for (int cate = 0; cate<4; cate++) {
				String path = String.format(PATH, part, cate);
				MMapFile newfile = new MMapFile(path, "rw");
				mapFile.put(cate, newfile.open().asRandomAccessFile());
			}
		}
		
		public void outputItem(ItemFeature item) throws IOException {
			RandomAccessFile outfile = mapFile.get(item.category);

			outfile.writeLong(item.itemId);
			outfile.writeInt(item.category);
			outfile.write(item.embedding);
		}

		@Override
		public void close() throws Exception {
			for (RandomAccessFile file : mapFile.values()) {
				try {
					file.close();
				} catch (IOException e) {
					LOG.error("", e);
				}
			}
		}
	}
	
	public static void convRawData2Blocks(InputConfig input, OutputConfig output) throws IOException {
		ItemFeature item = new ItemFeature();

		while (input.datafile.hasNext()) {
			input.datafile.nextItemFeature(item);
			output.outputItem(item);
		}
	}
	
	public static void main(String[] args) {
		for (int part = 0; part < 3; part++) {
			try (InputConfig input = new InputConfig(part); OutputConfig output = new OutputConfig(part)) {
				convRawData2Blocks(input, output);
			} catch (Exception e) {
				LOG.error("", e);
			}
		}
	}
}
