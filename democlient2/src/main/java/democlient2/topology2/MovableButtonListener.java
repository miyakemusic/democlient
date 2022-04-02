package democlient2.topology2;

import java.awt.Point;

public interface MovableButtonListener {

	void onClick();

	void onMoved(Point location);

	void onMoving(Point location);

	void onMoveStart(Point location);

}
