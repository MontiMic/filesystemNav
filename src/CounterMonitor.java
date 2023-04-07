import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

public class CounterMonitor {
    private final TreeSet<FileEntry> files = new TreeSet<>(Comparator.comparingLong(o -> -o.lines()));
    private final int[] buckets;
    private final int maxFiles;
    private final int bucketSize;

    public CounterMonitor(int nBuckets, int maxLines, int maxFiles) {
        buckets = new int[nBuckets];
        this.maxFiles = maxFiles;
        bucketSize = maxLines / (nBuckets - 1);
    }

    public synchronized void countedFile(File file, long lines) {
        files.add(new FileEntry(file.getPath(), lines));
        if (files.size() > maxFiles)
            files.remove(files.last());
        int bucketIdx = (int)Math.min(lines / bucketSize, buckets.length-1);
        buckets[bucketIdx]++;
    }

    public synchronized List<FileEntry> getTopFiles(int n) {
        return files.stream().limit(n).toList();
    }

    public synchronized List<Integer> getBuckets() {
        return Arrays.stream(buckets).boxed().toList();
    }

    public record FileEntry(String name, long lines) {}
}
