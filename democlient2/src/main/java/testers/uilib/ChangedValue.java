package testers.uilib;

public class ChangedValue {
	public ChangedValue(Long id2, Object value) {
		this.id = id2;
		this.value = value;
	}
	private Long id;
	private Object value;
	public Long getId() {
		return id;
	}
	public Object getValue() {
		return value;
	}
}