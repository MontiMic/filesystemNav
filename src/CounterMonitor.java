import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CounterMonitor {
    private final TreeSet<FileEntry> files = new TreeSet<>(Comparator.comparingLong(o -> -o.lines()));
    private final int[] buckets;
    private final int maxTopFiles;
    private final int bucketSize;

    public CounterMonitor(int nBuckets, int maxLines, int maxTopFiles) {
        this.buckets = new int[nBuckets];
        this.maxTopFiles = maxTopFiles;
        this.bucketSize = maxLines / (nBuckets - 1);
    }

    public synchronized void countedFile(File file, long lines) {
        this.files.add(new FileEntry(file.getPath(), lines));
        if (this.files.size() > this.maxTopFiles)
            this.files.remove(this.files.last());
        int bucketIdx = (int)Math.min(lines / this.bucketSize, this.buckets.length-1);
        this.buckets[bucketIdx]++;
    }

    public synchronized List<FileEntry> getTopFiles() {
        return this.files.stream().toList();
    }

    public synchronized Map<Integer, Integer> getBuckets() {
        return IntStream.range(0, this.buckets.length).boxed()
                .collect(Collectors.toMap(i -> i * this.bucketSize, i -> this.buckets[i]));
    }

}
