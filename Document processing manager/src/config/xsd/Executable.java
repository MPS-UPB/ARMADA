package config.xsd;
public class Executable {

	public String type;
	public String path;

	public Executable(String type, String execPath) {
		this.type = type;
		this.path = execPath;
	}

	@Override
	public String toString() {
		return type + " " + path;
	}

}
