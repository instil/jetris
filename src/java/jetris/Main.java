package jetris;

import javax.swing.SwingUtilities;

import jetris.model.GameModel;
import jetris.model.GameModelListener;
import jetris.model.GameController;
import jetris.model.PeriodicGameScheduler;
import jetris.view.GameView;

public class Main implements GameModelListener {

    private GameView view;

    public static void main(String[] args) {
        new Main().playGame();
    }

    private void playGame() {
        final GameController controller = new GameController(this, new PeriodicGameScheduler());
        createViewOnEventDispatchThread(controller);
    }

    private void createViewOnEventDispatchThread(final GameController controller) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view = new GameView(controller);
            }
        });
    }

    @Override
    public void modelChanged(final GameModel context) {
        assert view != null;
        if (SwingUtilities.isEventDispatchThread()) {
            view.modelChanged(context);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    view.modelChanged(context);
                }
            });
        }
    }
}
