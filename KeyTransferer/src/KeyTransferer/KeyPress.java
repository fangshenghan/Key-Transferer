package KeyTransferer;

public class KeyPress {
	public long time = 0;
	public int key;
	public boolean release = false;
	
	public boolean needOffset = true;
	
	public KeyPress(int key, long time, boolean release) {
		this.time = time;
		this.key = key;
		this.release = release;
	}
	
	public long getTotalTime() {
		if(this.needOffset) {
			return time + Main.offset + Main.offset2;
		}
		return time;
	}
}
