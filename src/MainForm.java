import javax.swing.*;
import java.util.Comparator;
import java.util.Map;
import java.util.SplittableRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MainForm {
    private JList bucketsList;
    private JList topList;
    private JTextField directoryText;
    private JButton startButton;
    private JButton pauseButton;
    private JButton stopButton;
    private JTextField nBucketsText;
    private JTextField maxLinesText;
    private JTextField maxTopFilesText;
    private JPanel rootPanel;
    private JPanel argsPanel;

    private ThreadedExplorer explorer;
    private DefaultListModel<String> bucketsModel = new DefaultListModel<>();
    private DefaultListModel<String> topModel = new DefaultListModel<>();
    private Status status = Status.STOP;

    public MainForm() {
        stopped();

        bucketsList.setModel(bucketsModel);
        topList.setModel(topModel);

        new Timer(10, e -> updateLists()).start();

        startButton.addActionListener(event -> {
            started();
            if (explorer == null) {
                explorer = new ThreadedExplorer(
                        directoryText.getText(),
                        Integer.parseInt(nBucketsText.getText()),
                        Integer.parseInt(maxLinesText.getText()),
                        Integer.parseInt(maxTopFilesText.getText())
                );
                explorer.setOnStoppedCallback(() -> SwingUtilities.invokeLater(() -> {
                    updateStatus();
                    updateLists();
                }));
            }
            explorer.start();
        });
        pauseButton.addActionListener(e -> {
            status = Status.PAUSE;
            explorer.stop();
        });
        stopButton.addActionListener(e -> {
            status = Status.STOP;
            explorer.stop();
            updateStatus();
        });
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void updateStatus() {
        if (explorer == null || !explorer.isStopped())
            return;
        if (explorer.isDone())
            status = Status.STOP;
        if (status == Status.STOP) {
            explorer = null;
            stopped();
        } else if (status == Status.PAUSE) {
            paused();
        }
    }

    private void updateLists() {
        if (explorer == null)
            return;

        bucketsModel.clear();
        var buckets = explorer.getBuckets().entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey)).toList();
        bucketsModel.addAll(IntStream.range(0, buckets.size()).mapToObj(i ->
                buckets.get(i).getKey()+".."+(i < buckets.size()-1 ? buckets.get(i+1).getKey() : "")+": "+buckets.get(i).getValue()).toList());

        topModel.clear();
        topModel.addAll(explorer.getTopFiles().stream().map(e -> e.lines() + ": " + e.name()).toList());
    }

    private void started() {
        argsPanel.setEnabled(false);
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        stopButton.setEnabled(true);

        bucketsModel.clear();
        topModel.clear();
    }

    private void paused() {
        argsPanel.setEnabled(false);
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    private void stopped() {
        argsPanel.setEnabled(true);
        startButton.setEnabled(true);
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    enum Status { PLAY, PAUSE, STOP };
}
