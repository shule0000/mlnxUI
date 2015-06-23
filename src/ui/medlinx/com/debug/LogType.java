package ui.medlinx.com.debug;

public enum LogType {
	EMPUTENT("重要"), DEBUG("调试"), WARN("警告"), ERROR("错误");

	private String describe;

	private LogType(String describe) {
		this.describe = describe;
	}

	@Override
	public String toString() {
		return describe;
	}
}
