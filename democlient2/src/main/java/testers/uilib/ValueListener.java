package testers.uilib;

public interface ValueListener {
	void onChange(Long id, Object value);

	void onEnableChange(Long id, boolean enabled);

}
