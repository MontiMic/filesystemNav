import javax.swing.*;

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
    private DefaultListModel<Integer> bucketsModel = new DefaultListModel<>();
    private DefaultListModel<String> topModel = new DefaultListModel<>();

    public MainForm() {
        stopped();

        bucketsList.setModel(bucketsModel);
        topList.setModel(topModel);

        startButton.addActionListener(e -> {
            started();
            if (explorer == null) {
                explorer = new ThreadedExplorer(
                        directoryText.getText(),
                        Integer.parseInt(nBucketsText.getText()),
                        Integer.parseInt(maxLinesText.getText()),
                        Integer.parseInt(maxTopFilesText.getText())
                );
            }
            SwingUtilities.invokeLater(() -> {
                explorer.start();
                explorer.await();
                updateLists();
                if (explorer == null || explorer.isDone()) {
                    explorer = null;
                    stopped();
                } else {
                    paused();
                }
            });
        });
        pauseButton.addActionListener(e -> {
            explorer.stop();
        });
        stopButton.addActionListener(e -> {
            explorer.stop();
            explorer = null;
        });
    }

    public JPanel getRootPanel() {
        return this.rootPanel;
    }

    private void updateLists() {
        if (explorer == null)
            return;

        bucketsModel.clear();
        bucketsModel.addAll(explorer.getBuckets());

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
}
