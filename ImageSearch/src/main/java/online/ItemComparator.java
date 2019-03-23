package online;

import java.nio.ByteBuffer;

import org.apache.log4j.Logger;

import utils.ByteUtils;

public class ItemComparator {
	static final Logger LOG = Logger.getLogger(ItemComparator.class);
	
	static byte[] NUM_SETS = new byte[256];

	static int EMBEDDING_SIZE = 4096;

	static {
		for (int x = 0; x < 256; x++) {
			NUM_SETS[x] = (byte) (8 - ByteUtils.countSetBits(x));
		}
	}
	
	public interface Compare {
		public float similarity(byte[] src, byte[] dst);
	}
	
	public static class HammingDist implements Compare {

		@Override
		public float similarity(byte[] src, byte[] dst) {
			return hammingDist(src, dst) / (float) (8 * src.length);
		}
	}

	public static int hammingDist(byte[] src, byte[] dst) {
		int ret = 0;
		for (int x = 0; x < src.length; x++) {
			// LOG.info(String.format("%s vs %s", ByteUtils.bytesToHex(src[x]), ByteUtils.bytesToHex(dst[x])));
			// LOG.info(String.format("%s vs %s = %s", ByteUtils.bytesToHex(src[x]), ByteUtils.bytesToHex(dst[x]), tmp.tou));
			ret += NUM_SETS[Byte.toUnsignedInt((byte) (src[x] ^ dst[x]))];
		}
		return ret;
	}

	public static void main(String[] args) {
//		for (int x = 0; x < 256; x++) {
//			System.out.println(x + ": " + NUM_SETS[x]);
//		}
		
		byte[] src = ByteBuffer.allocate(4).putInt(1).array();
		byte[] dst = ByteBuffer.allocate(4).putInt(2).array();
		
		Compare comp = new HammingDist();
		
		System.out.println(comp.similarity(src, dst));
	}
}
