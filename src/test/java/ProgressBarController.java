import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class ProgressBarController {
    private Task copyWorker;

    @FXML
    private ProgressBar progressBar;




    @FXML
    private void onButtonClick() {

        //progressBar.setProgress(0);
        copyWorker = createWorker();

        progressBar.progressProperty().unbind();
        progressBar.progressProperty().bind(copyWorker.progressProperty());

        new Thread(copyWorker).start();
    }


    public Task createWorker() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(2000);
                    updateMessage("2000 milliseconds");
                    updateProgress(i + 1, 10);

                    System.out.println(progressBar.getProgress());

                }
                System.out.println("done");
                progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
                return true;
            }
        };
    }
}