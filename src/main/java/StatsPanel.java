package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class StatsPanel extends JPanel {
    private final JTable table;
    private final JComboBox<String> aspectCombo;
    private final JCheckBox showLabelsCheck;
    private final JSplitPane split;

    public StatsPanel(JTable entryTable) {
        super(new BorderLayout());
        this.table = entryTable;

        // --- top controls: aspect selector + toggle
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        aspectCombo = new JComboBox<>(new String[]{"Vendor","Model","Department","Item Type","Owner"});
        showLabelsCheck = new JCheckBox("Show slice labels", true);
        controls.add(new JLabel("Aspect:"));
        controls.add(aspectCombo);
        controls.add(showLabelsCheck);
        add(controls, BorderLayout.NORTH);

        // --- center: split pane containing chart | legend
        ChartPanel initialChart = new ChartPanel(
                ChartFactory.createPieChart("", new DefaultPieDataset(), false, true, false)
        );
        JList<LegendItem> initialLegend = new JList<>(new DefaultListModel<>());
        split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                initialChart,
                new JScrollPane(initialLegend));
        split.setResizeWeight(0.75);
        add(split, BorderLayout.CENTER);

        // rebuild when controls change
        ActionListener rebuildListener = e -> rebuild();
        aspectCombo.addActionListener(rebuildListener);
        showLabelsCheck.addActionListener(rebuildListener);

        // initial draw
        rebuild();
    }

    private void rebuild() {
        // 1) tally up values for selected column
        String aspect = (String) aspectCombo.getSelectedItem();
        TableModel model = table.getModel();
        int colIndex = -1;
        for (int i = 0; i < model.getColumnCount(); i++) {
            if (aspect.equals(model.getColumnName(i))) { colIndex = i; break; }
        }
        Map<String,Integer> counts = new HashMap<>();
        if (colIndex >= 0) {
            for (int row = 0; row < model.getRowCount(); row++) {
                Object v = model.getValueAt(row, colIndex);
                String key = (v != null && !v.toString().isEmpty()) ? v.toString() : "(none)";
                counts.merge(key, 1, Integer::sum);
            }
        }

        // 2) build pie dataset & chart (legend turned off)
        DefaultPieDataset dataset = new DefaultPieDataset();
        counts.forEach(dataset::setValue);
        JFreeChart chart = ChartFactory.createPieChart(
                "Distribution by " + aspect,
                dataset,
                false,    // no built-in legend
                true,
                false
        );
        PiePlot plot = (PiePlot) chart.getPlot();
        // on-slice labels controlled by checkbox
        if (showLabelsCheck.isSelected()) {
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {2}"));
        } else {
            plot.setLabelGenerator(null);
        }
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseWheelEnabled(true);

        // 3) build custom legend list model
        DefaultListModel<LegendItem> lm = new DefaultListModel<>();
        // sort descending by count
        List<LegendItem> items = new ArrayList<>();
        counts.forEach((k, c) -> items.add(new LegendItem(k, c)));
        items.sort((a, b) -> Integer.compare(b.count, a.count));
        items.forEach(lm::addElement);

        JList<LegendItem> legendList = new JList<>(lm);
        legendList.setCellRenderer(new LegendItemRenderer(plot));
        legendList.setVisibleRowCount(10);
        JScrollPane legendScroll = new JScrollPane(legendList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        legendScroll.setBorder(BorderFactory.createTitledBorder("Legend"));

        // 4) swap into split pane
        split.setLeftComponent(chartPanel);
        split.setRightComponent(legendScroll);
        split.revalidate();
    }

    // simple holder for legend entries
    private static class LegendItem {
        final String key;
        final int count;
        LegendItem(String key, int count) { this.key = key; this.count = count; }
        @Override public String toString() { return key + " (" + count + ")"; }
    }

    // renderer that draws a colored dot + text
    private static class LegendItemRenderer extends JLabel implements ListCellRenderer<LegendItem> {
        private final PiePlot plot;
        LegendItemRenderer(PiePlot plot) {
            this.plot = plot;
            setOpaque(true);
            setIconTextGap(8);
        }
        @Override
        public Component getListCellRendererComponent(JList<? extends LegendItem> list,
                                                      LegendItem value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            // dot color = slice paint
            Paint p = plot.getSectionPaint(value.key);
            if (p instanceof Color) {
                setIcon(new ColorIcon((Color)p, 10, 10));
            } else {
                setIcon(null);
            }
            setText(value.toString());

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            return this;
        }
    }

    // small colored circle icon
    private static class ColorIcon implements Icon {
        private final Color color;
        private final int w, h;
        ColorIcon(Color c, int w, int h) { this.color = c; this.w = w; this.h = h; }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillOval(x, y, w, h);
        }
        @Override public int getIconWidth()  { return w; }
        @Override public int getIconHeight() { return h; }
    }
}
