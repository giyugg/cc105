package ClaudeAI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Random;

/**
 * SortingVisualizer
 * -------------------
 * A single-file Java Swing app that animates several classic sorting
 * algorithms so you can watch how each one works.
 *
 * This version focuses purely on making the visual "core" (the render
 * loop / VisualizerPanel) nicer to look at, and adds an in-app Debug
 * panel + console tracing. No OS-specific tuning is done on purpose —
 * this targets "run it in IntelliJ and watch it" rather than
 * squeezing out platform-specific rendering performance.
 *
 * Run in IntelliJ:
 *   1. File > New > Project from Existing Sources... (or just drop this
 *      file into src/ClaudeAI/SortingVisualizer.java in any project).
 *   2. Make sure the package folder is literally "ClaudeAI" under a
 *      source root (src/ClaudeAI/SortingVisualizer.java).
 *   3. Right click the file > Run 'SortingVisualizer.main()'.
 *   4. Check the "Debug" checkbox in the toolbar to see live index /
 *      value labels on the bars and step-by-step tracing in the
 *      IntelliJ "Run" console.
 *
 * Compile by hand:  javac ClaudeAI/SortingVisualizer.java
 * Run by hand:       java ClaudeAI.SortingVisualizer
 */
public class SortingVisualizer extends JFrame {

    // ---- Shared state -------------------------------------------------
    private final VisualizerPanel panel;
    private final JComboBox<String> algoBox;
    private final JSlider speedSlider;
    private final JSlider sizeSlider;
    private final JButton startButton;
    private final JButton shuffleButton;
    private final JButton stopButton;
    private final JCheckBox debugCheckBox;
    private final JLabel statusLabel;
    private final JLabel debugLabel;

    private volatile boolean sorting = false;
    private volatile boolean stopRequested = false;
    private Thread workerThread;

    private static final String[] ALGORITHMS = {
            "Bubble Sort",
            "Selection Sort",
            "Insertion Sort",
            "Merge Sort",
            "Quick Sort",
            "Heap Sort",
            "Shell Sort",
            "Cocktail Shaker Sort"
    };

    public SortingVisualizer() {
        super("Sorting Algorithm Visualizer");

        panel = new VisualizerPanel(80);

        algoBox = new JComboBox<>(ALGORITHMS);
        speedSlider = new JSlider(1, 210, 60);
        sizeSlider = new JSlider(10, 2500, 80);
        startButton = new JButton("Start");
        shuffleButton = new JButton("Shuffle");
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        debugCheckBox = new JCheckBox("Debug");
        statusLabel = new JLabel("Ready");
        debugLabel = new JLabel(" ");
        debugLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        debugLabel.setForeground(new Color(120, 220, 255));

        buildUI();
        wireEvents();
    }

    private void buildUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(18, 19, 24));

        JPanel controls = new JPanel();
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        controls.setBackground(new Color(28, 30, 38));

        controls.add(styledLabel("Algorithm:"));
        controls.add(algoBox);

        controls.add(styledLabel("Bars:"));
        sizeSlider.setPreferredSize(new Dimension(120, 20));
        controls.add(sizeSlider);

        controls.add(styledLabel("Speed:"));
        speedSlider.setPreferredSize(new Dimension(140, 20));
        controls.add(speedSlider);

        controls.add(shuffleButton);
        controls.add(startButton);
        controls.add(stopButton);
        controls.add(debugCheckBox);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(28, 30, 38));
        statusLabel.setForeground(new Color(220, 220, 230));
        debugLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 4, 10));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        statusBar.add(statusLabel, BorderLayout.NORTH);
        statusBar.add(debugLabel, BorderLayout.SOUTH);

        add(controls, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        setSize(1000, 650);
        setLocationRelativeTo(null);
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(200, 200, 210));
        return l;
    }

    private void wireEvents() {
        shuffleButton.addActionListener((ActionEvent e) -> {
            if (sorting) return;
            panel.shuffle();
            statusLabel.setText("Shuffled " + panel.length() + " bars");
        });

        sizeSlider.addChangeListener(e -> {
            if (sorting) return;
            if (!sizeSlider.getValueIsAdjusting()) {
                panel.resize(sizeSlider.getValue());
            }
        });

        debugCheckBox.addActionListener(e -> {
            panel.setDebugMode(debugCheckBox.isSelected());
            if (!debugCheckBox.isSelected()) {
                debugLabel.setText(" ");
            }
        });

        startButton.addActionListener((ActionEvent e) -> startSort());
        stopButton.addActionListener((ActionEvent e) -> {
            stopRequested = true;
            statusLabel.setText("Stopping...");
        });
    }

    private void startSort() {
        if (sorting) return;
        sorting = true;
        stopRequested = false;
        startButton.setEnabled(false);
        shuffleButton.setEnabled(false);
        sizeSlider.setEnabled(false);
        algoBox.setEnabled(false);
        stopButton.setEnabled(true);

        String algo = (String) algoBox.getSelectedItem();
        statusLabel.setText("Sorting with " + algo + "...");
        if (debugCheckBox.isSelected()) {
            System.out.println("[DEBUG] === Starting " + algo + " on " + panel.length() + " elements ===");
        }

        workerThread = new Thread(() -> {
            long start = System.currentTimeMillis();
            try {
                Sorter sorter = new Sorter(panel, this::isStopRequested, this::currentDelayMs, this::onStep);
                switch (algo) {
                    case "Bubble Sort": sorter.bubbleSort(); break;
                    case "Selection Sort": sorter.selectionSort(); break;
                    case "Insertion Sort": sorter.insertionSort(); break;
                    case "Merge Sort": sorter.mergeSort(); break;
                    case "Quick Sort": sorter.quickSort(); break;
                    case "Heap Sort": sorter.heapSort(); break;
                    case "Shell Sort": sorter.shellSort(); break;
                    case "Cocktail Shaker Sort": sorter.cocktailSort(); break;
                }
                if (!stopRequested) {
                    panel.flashDoneAnimation(this::isStopRequested, this::currentDelayMs);
                }
            } catch (InterruptedSortException ignored) {
                // sorting was stopped by user
            } finally {
                long elapsed = System.currentTimeMillis() - start;
                SwingUtilities.invokeLater(() -> {
                    sorting = false;
                    startButton.setEnabled(true);
                    shuffleButton.setEnabled(true);
                    sizeSlider.setEnabled(true);
                    algoBox.setEnabled(true);
                    stopButton.setEnabled(false);
                    panel.clearHighlights();
                    if (stopRequested) {
                        statusLabel.setText("Stopped.");
                        if (debugCheckBox.isSelected()) {
                            System.out.println("[DEBUG] Sort interrupted by user.");
                        }
                    } else {
                        statusLabel.setText(algo + " finished in " + elapsed + " ms  "
                                + "(comparisons: " + panel.getComparisons()
                                + ", swaps/writes: " + panel.getWrites() + ")");
                        if (debugCheckBox.isSelected()) {
                            System.out.println("[DEBUG] === Finished " + algo + " in " + elapsed + " ms | "
                                    + "comparisons=" + panel.getComparisons()
                                    + " writes=" + panel.getWrites() + " ===");
                        }
                    }
                });
            }
        }, "sorter-thread");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    /** Called on every algorithm step to refresh the debug status line + console. */
    private void onStep(String context, int i, int j) {
        if (!debugCheckBox.isSelected()) return;
        String text = String.format("i=%-4s j=%-4s comparisons=%-6d writes=%-6d delay=%dms",
                i >= 0 ? String.valueOf(i) : "-", j >= 0 ? String.valueOf(j) : "-",
                panel.getComparisons(), panel.getWrites(), currentDelayMs());
        SwingUtilities.invokeLater(() -> debugLabel.setText(text));
        System.out.println("[DEBUG] " + context + "  " + text);
    }

    private boolean isStopRequested() {
        return stopRequested;
    }

    private int currentDelayMs() {
        // Slider goes 1 (fast) .. 200 (slow). Invert so higher slider = faster.
        int v = speedSlider.getValue();
        return 210 - v; // roughly 10ms..209ms
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SortingVisualizer().setVisible(true));
    }

    // =====================================================================
    // Thrown internally to unwind the algorithm's call stack when the user
    // presses Stop mid-sort.
    // =====================================================================
    static class InterruptedSortException extends RuntimeException {
    }

    // Simple 3-arg callback used to report a step for the debug overlay/console.
    interface StepListener {
        void onStep(String context, int i, int j);
    }

    // =====================================================================
    // The drawing surface: holds the array of values and knows how to
    // paint them as bars, including highlight colors for the indices
    // currently being compared / swapped / pivoted.
    //
    // This is the "core" that got the visual pass: gradient bars, rounded
    // corners, soft glow on active bars, a subtle baseline/grid, and an
    // optional debug overlay that prints index/value labels above bars.
    // =====================================================================
    static class VisualizerPanel extends JPanel {
        private int[] data;
        private int highlightA = -1, highlightB = -1, pivotIndex = -1;
        private int sortedBoundary = -1; // indices < this are considered finalized (used for final sweep)
        private long comparisons = 0;
        private long writes = 0;
        private boolean debugMode = false;
        private final Random random = new Random();

        private static final Color BG_TOP = new Color(20, 22, 30);
        private static final Color BG_BOTTOM = new Color(12, 13, 18);
        private static final Color BAR_DEFAULT_TOP = new Color(110, 180, 255);
        private static final Color BAR_DEFAULT_BOTTOM = new Color(60, 110, 210);
        private static final Color BAR_ACTIVE_TOP = new Color(255, 130, 130);
        private static final Color BAR_ACTIVE_BOTTOM = new Color(220, 60, 90);
        private static final Color BAR_PIVOT_TOP = new Color(255, 214, 90);
        private static final Color BAR_PIVOT_BOTTOM = new Color(240, 160, 20);
        private static final Color BAR_DONE_TOP = new Color(120, 240, 170);
        private static final Color BAR_DONE_BOTTOM = new Color(50, 190, 120);
        private static final Color GRID_LINE = new Color(255, 255, 255, 14);

        VisualizerPanel(int n) {
            setBackground(BG_BOTTOM);
            data = new int[n];
            shuffle();
        }

        void setDebugMode(boolean on) {
            this.debugMode = on;
            repaint();
        }

        int length() {
            return data.length;
        }

        long getComparisons() {
            return comparisons;
        }

        long getWrites() {
            return writes;
        }

        void resize(int n) {
            data = new int[n];
            shuffle();
        }

        void shuffle() {
            for (int i = 0; i < data.length; i++) {
                data[i] = 20 + random.nextInt(980);
            }
            comparisons = 0;
            writes = 0;
            clearHighlights();
            repaint();
        }

        void clearHighlights() {
            highlightA = -1;
            highlightB = -1;
            pivotIndex = -1;
            sortedBoundary = -1;
            repaint();
        }

        // ---- Operations used by the Sorter (each repaints + sleeps) ----

        int get(int i) {
            return data[i];
        }

        void compare(int i, int j) {
            comparisons++;
            highlightA = i;
            highlightB = j;
            pivotIndex = -1;
        }

        void markPivot(int i) {
            pivotIndex = i;
        }

        void swap(int i, int j) {
            writes += 3;
            int tmp = data[i];
            data[i] = data[j];
            data[j] = tmp;
            highlightA = i;
            highlightB = j;
        }

        void set(int i, int value) {
            writes++;
            data[i] = value;
            highlightA = i;
        }

        void flashDoneAnimation(java.util.function.BooleanSupplier stopCheck,
                                java.util.function.IntSupplier delaySupplier) {
            for (int i = 0; i < data.length; i++) {
                if (stopCheck.getAsBoolean()) return;
                sortedBoundary = i + 1;
                repaint();
                sleepQuiet(Math.max(1, delaySupplier.getAsInt() / 8));
            }
        }

        void sleepQuiet(int ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedSortException();
            }
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth();
            int h = getHeight();
            int n = data.length;
            if (n == 0) return;

            // Soft vertical background gradient instead of a flat fill.
            g.setPaint(new GradientPaint(0, 0, BG_TOP, 0, h, BG_BOTTOM));
            g.fillRect(0, 0, w, h);

            // Faint horizontal grid lines for a bit of "chart" texture.
            g.setColor(GRID_LINE);
            int gridLines = 6;
            for (int i = 1; i < gridLines; i++) {
                int y = h * i / gridLines;
                g.drawLine(0, y, w, y);
            }

            double barWidth = (double) w / n;
            int max = 1;
            for (int v : data) max = Math.max(max, v);

            boolean showLabels = debugMode && n <= 60 && barWidth > 14;
            Font labelFont = new Font(Font.MONOSPACED, Font.PLAIN, Math.max(8, Math.min(11, (int) (barWidth * 0.4))));
            g.setFont(labelFont);
            FontMetrics fm = g.getFontMetrics();

            for (int i = 0; i < n; i++) {
                double barHeight = ((double) data[i] / max) * (h - 30);
                double x = i * barWidth;
                double y = h - barHeight - 4;

                Color top, bottom;
                boolean active = false;
                if (sortedBoundary >= 0 && i < sortedBoundary) {
                    top = BAR_DONE_TOP;
                    bottom = BAR_DONE_BOTTOM;
                } else if (i == pivotIndex) {
                    top = BAR_PIVOT_TOP;
                    bottom = BAR_PIVOT_BOTTOM;
                    active = true;
                } else if (i == highlightA || i == highlightB) {
                    top = BAR_ACTIVE_TOP;
                    bottom = BAR_ACTIVE_BOTTOM;
                    active = true;
                } else {
                    top = BAR_DEFAULT_TOP;
                    bottom = BAR_DEFAULT_BOTTOM;
                }

                double bw = Math.max(1.5, barWidth - 2);
                float arc = (float) Math.min(6, bw * 0.4);

                // Soft glow behind active bars.
                if (active) {
                    g.setColor(new Color(top.getRed(), top.getGreen(), top.getBlue(), 60));
                    g.fill(new RoundRectangle2D.Double(x - 1.5, y - 3, bw + 3, barHeight + 7, arc + 3, arc + 3));
                }

                // Drop shadow for depth.
                g.setColor(new Color(0, 0, 0, 70));
                g.fill(new RoundRectangle2D.Double(x + 1.5, y + 2, bw, barHeight, arc, arc));

                // The bar itself, vertically gradient-shaded top-to-bottom.
                g.setPaint(new GradientPaint((float) x, (float) y, top, (float) x, (float) (y + barHeight), bottom));
                g.fill(new RoundRectangle2D.Double(x, y, bw, barHeight, arc, arc));

                // Thin highlight rim on top edge for a glassy look.
                g.setColor(new Color(255, 255, 255, 60));
                g.drawLine((int) (x + 1), (int) y, (int) (x + bw - 1), (int) y);

                if (showLabels) {
                    String label = String.valueOf(data[i]);
                    int lw = fm.stringWidth(label);
                    if (lw < bw + 4) {
                        g.setColor(new Color(230, 230, 240));
                        g.drawString(label, (float) (x + (bw - lw) / 2.0), (float) (y - 4));
                    }
                }
            }

            // Baseline.
            g.setColor(new Color(255, 255, 255, 40));
            g.drawLine(0, h - 3, w, h - 3);
        }
    }

    // =====================================================================
    // Contains the actual sorting algorithms. Each one operates on the
    // VisualizerPanel's data array through compare/swap/set so that every
    // meaningful step is animated, and now also reports each step to a
    // StepListener so the UI can show a live debug readout.
    // =====================================================================
    static class Sorter {
        private final VisualizerPanel p;
        private final java.util.function.BooleanSupplier stopCheck;
        private final java.util.function.IntSupplier delaySupplier;
        private final StepListener stepListener;

        Sorter(VisualizerPanel p,
               java.util.function.BooleanSupplier stopCheck,
               java.util.function.IntSupplier delaySupplier,
               StepListener stepListener) {
            this.p = p;
            this.stopCheck = stopCheck;
            this.delaySupplier = delaySupplier;
            this.stepListener = stepListener;
        }

        private void step() {
            if (stopCheck.getAsBoolean()) throw new InterruptedSortException();
            SwingUtilities.invokeLater(p::repaint);
            p.sleepQuiet(delaySupplier.getAsInt());
        }

        private void step(String context, int i, int j) {
            if (stepListener != null) stepListener.onStep(context, i, j);
            step();
        }

        private boolean less(int i, int j) {
            p.compare(i, j);
            step("compare", i, j);
            return p.get(i) < p.get(j);
        }

        private void swap(int i, int j) {
            p.swap(i, j);
            step("swap", i, j);
        }

        // ---------------- Bubble Sort ----------------
        void bubbleSort() {
            int n = p.length();
            for (int i = 0; i < n - 1; i++) {
                boolean swapped = false;
                for (int j = 0; j < n - i - 1; j++) {
                    if (!less(j, j + 1)) {
                        swap(j, j + 1);
                        swapped = true;
                    }
                }
                if (!swapped) break;
            }
        }

        // ---------------- Selection Sort ----------------
        void selectionSort() {
            int n = p.length();
            for (int i = 0; i < n - 1; i++) {
                int minIdx = i;
                p.markPivot(minIdx);
                for (int j = i + 1; j < n; j++) {
                    if (less(j, minIdx)) {
                        minIdx = j;
                        p.markPivot(minIdx);
                    }
                }
                if (minIdx != i) swap(i, minIdx);
            }
        }

        // ---------------- Insertion Sort ----------------
        void insertionSort() {
            int n = p.length();
            for (int i = 1; i < n; i++) {
                int j = i;
                while (j > 0) {
                    p.compare(j - 1, j);
                    step("compare", j - 1, j);
                    if (p.get(j - 1) > p.get(j)) {
                        swap(j - 1, j);
                        j--;
                    } else {
                        break;
                    }
                }
            }
        }

        // ---------------- Shell Sort ----------------
        void shellSort() {
            int n = p.length();
            for (int gap = n / 2; gap > 0; gap /= 2) {
                for (int i = gap; i < n; i++) {
                    int j = i;
                    while (j >= gap) {
                        p.compare(j - gap, j);
                        step("compare", j - gap, j);
                        if (p.get(j - gap) > p.get(j)) {
                            swap(j - gap, j);
                            j -= gap;
                        } else {
                            break;
                        }
                    }
                }
            }
        }

        // ---------------- Cocktail Shaker Sort ----------------
        void cocktailSort() {
            int n = p.length();
            int start = 0, end = n - 1;
            boolean swapped = true;
            while (swapped) {
                swapped = false;
                for (int i = start; i < end; i++) {
                    if (!less(i, i + 1)) {
                        swap(i, i + 1);
                        swapped = true;
                    }
                }
                end--;
                if (!swapped) break;
                swapped = false;
                for (int i = end; i > start; i--) {
                    if (!less(i - 1, i)) {
                        swap(i - 1, i);
                        swapped = true;
                    }
                }
                start++;
            }
        }

        // ---------------- Merge Sort ----------------
        void mergeSort() {
            mergeSortRec(0, p.length() - 1);
        }

        private void mergeSortRec(int lo, int hi) {
            if (lo >= hi) return;
            int mid = (lo + hi) / 2;
            mergeSortRec(lo, mid);
            mergeSortRec(mid + 1, hi);
            merge(lo, mid, hi);
        }

        private void merge(int lo, int mid, int hi) {
            int[] left = new int[mid - lo + 1];
            int[] right = new int[hi - mid];
            for (int i = 0; i < left.length; i++) left[i] = p.get(lo + i);
            for (int i = 0; i < right.length; i++) right[i] = p.get(mid + 1 + i);

            int i = 0, j = 0, k = lo;
            while (i < left.length && j < right.length) {
                if (stopCheck.getAsBoolean()) throw new InterruptedSortException();
                p.compare(lo + i, mid + 1 + j);
                step("merge-compare", lo + i, mid + 1 + j);
                if (left[i] <= right[j]) {
                    p.set(k, left[i]);
                    i++;
                } else {
                    p.set(k, right[j]);
                    j++;
                }
                step("merge-write", k, -1);
                k++;
            }
            while (i < left.length) {
                p.set(k, left[i]);
                step("merge-write", k, -1);
                i++;
                k++;
            }
            while (j < right.length) {
                p.set(k, right[j]);
                step("merge-write", k, -1);
                j++;
                k++;
            }
        }

        // ---------------- Quick Sort ----------------
        void quickSort() {
            quickSortRec(0, p.length() - 1);
        }

        private void quickSortRec(int lo, int hi) {
            if (lo >= hi) return;
            int pivotIdx = partition(lo, hi);
            quickSortRec(lo, pivotIdx - 1);
            quickSortRec(pivotIdx + 1, hi);
        }

        private int partition(int lo, int hi) {
            int pivotValue = p.get(hi);
            p.markPivot(hi);
            step("pivot", hi, -1);
            int i = lo - 1;
            for (int j = lo; j < hi; j++) {
                p.compare(j, hi);
                step("compare", j, hi);
                if (p.get(j) < pivotValue) {
                    i++;
                    swap(i, j);
                }
            }
            swap(i + 1, hi);
            p.markPivot(-1);
            return i + 1;
        }

        // ---------------- Heap Sort ----------------
        void heapSort() {
            int n = p.length();
            for (int i = n / 2 - 1; i >= 0; i--) {
                heapify(n, i);
            }
            for (int i = n - 1; i > 0; i--) {
                swap(0, i);
                heapify(i, 0);
            }
        }

        private void heapify(int n, int root) {
            int largest = root;
            int left = 2 * root + 1;
            int right = 2 * root + 2;

            if (left < n) {
                p.compare(left, largest);
                step("compare", left, largest);
                if (p.get(left) > p.get(largest)) largest = left;
            }
            if (right < n) {
                p.compare(right, largest);
                step("compare", right, largest);
                if (p.get(right) > p.get(largest)) largest = right;
            }
            if (largest != root) {
                swap(root, largest);
                heapify(n, largest);
            }
        }
    }
}