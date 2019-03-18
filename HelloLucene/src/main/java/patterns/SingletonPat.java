package patterns;


// https://www.geeksforgeeks.org/java-singleton-design-pattern-practices-examples/
public class SingletonPat {
	private static SingletonPat instance;
	
	private SingletonPat() {
	}
	
	public static SingletonPat getInstance() {
		if (instance == null) {
			synchronized (SingletonPat.class) {
				if (instance == null) {
					instance = new SingletonPat();
				}
			}
		}
		return instance;
	}
}
