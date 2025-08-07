package org.example;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class StatsPanel extends JPanel {
    private static final String PIE     = "Pie Chart";
    private static final String BAR     = "Bar Chart";
    private static final String STACKED = "Stacked Bar Chart";

    private final JTable table;
    private final JComboBox<String> aspectCombo;
    private final JComboBox<String> chartTypeCombo;
    private final JCheckBox showLabelsCheck;
    private final JSplitPane split;

    public StatsPanel(JTable entryTable) {
        super(new BorderLayout());
        this.table = entryTable;

        // --- Top controls
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        controls.add(new JLabel("Aspect:"));
        aspectCombo = new JComboBox<>(new String[]{"Vendor", "Model", "Department", "Item Type", "Owner"});
        controls.add(aspectCombo);

        controls.add(new JLabel("Chart Type:"));
        chartTypeCombo = new JComboBox<>(new String[]{PIE, BAR, STACKED});
        controls.add(chartTypeCombo);

        showLabelsCheck = new JCheckBox("Show Labels", true);
        controls.add(showLabelsCheck);

        add(controls, BorderLayout.NORTH);

        // --- Placeholder chart + legend
        ChartPanel placeholderChart = new ChartPanel(
                ChartFactory.createPieChart("", new DefaultPieDataset(), false, true, false)
        );
        JList<String> placeholderList = new JList<>(new DefaultListModel<>());
        split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                placeholderChart,
                new JScrollPane(placeholderList)
        );
        split.setResizeWeight(0.75);
        add(split, BorderLayout.CENTER);

        // --- Rebuild on any control change
        ActionListener rebuild = e -> rebuildChart();
        aspectCombo.addActionListener(rebuild);
        chartTypeCombo.addActionListener(rebuild);
        showLabelsCheck.addActionListener(rebuild);

        // --- Initial draw
        rebuildChart();
    }

    private void rebuildChart() {
        String aspect = (String) aspectCombo.getSelectedItem();
        String type   = (String) chartTypeCombo.getSelectedItem();

        // 1) Tally values for the chosen aspect
        Map<String, Integer> counts = new HashMap<>();
        TableModel m = table.getModel();
        int col = findColumn(m, aspect);
        if (col >= 0) {
            for (int r = 0; r < m.getRowCount(); r++) {
                Object v = m.getValueAt(r, col);
                String key = (v != null && !v.toString().isEmpty()) ? v.toString() : "(none)";
                counts.merge(key, 1, Integer::sum);
            }
        }

        // 2) Create the correct chart
        JFreeChart chart;
        ChartPanel cp;
        if (STACKED.equals(type)) {
            // **Stacked Bar**: one series per category, stacked along the 'aspect' axis
            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            counts.forEach((categoryValue, count) ->
                    ds.addValue(count, categoryValue, aspect)
            );

            chart = ChartFactory.createStackedBarChart(
                    aspect + " Breakdown",  // title
                    aspect,                 // domain axis label
                    "Count",                // range axis label
                    ds,                     // dataset
                    PlotOrientation.VERTICAL,
                    true,  // legend
                    true,  // tooltips
                    false  // URLs
            );
            CategoryPlot stackedPlot = chart.getCategoryPlot();
            BarRenderer stackedRenderer = (BarRenderer) stackedPlot.getRenderer();
            stackedRenderer.setDefaultItemLabelGenerator(
                    new StandardCategoryItemLabelGenerator()
            );
            stackedRenderer.setDefaultItemLabelsVisible(
                    showLabelsCheck.isSelected()
            );

            cp = new ChartPanel(chart);
            cp.setPreferredSize(new Dimension(600, 200 + counts.size() * 30));

        } else if (BAR.equals(type)) {
            // Regular bar chart: one series "Count"
            DefaultCategoryDataset ds = new DefaultCategoryDataset();
            counts.forEach((k, v) -> ds.addValue(v, "Count", k));

            chart = ChartFactory.createBarChart(
                    "Count by " + aspect,
                    aspect,
                    "Count",
                    ds,
                    PlotOrientation.VERTICAL,
                    false, // no legend
                    true,
                    false
            );
            CategoryPlot barPlot = chart.getCategoryPlot();
            BarRenderer barRenderer = (BarRenderer) barPlot.getRenderer();
            barRenderer.setDefaultItemLabelGenerator(
                    new StandardCategoryItemLabelGenerator()
            );
            barRenderer.setDefaultItemLabelsVisible(
                    showLabelsCheck.isSelected()
            );

            cp = new ChartPanel(chart);
            cp.setPreferredSize(new Dimension(600, 200 + counts.size() * 30));

        } else {
            // Pie chart
            DefaultPieDataset ds = new DefaultPieDataset();
            counts.forEach(ds::setValue);

            chart = ChartFactory.createPieChart(
                    "Distribution by " + aspect,
                    ds,
                    false, // no built-in legend
                    true,
                    false
            );
            PiePlot piePlot = (PiePlot) chart.getPlot();
            piePlot.setLabelGenerator(
                    showLabelsCheck.isSelected()
                            ? new StandardPieSectionLabelGenerator("{0}: {2}")
                            : null
            );

            // **Pie chart sizing**: let it fill the split-pane area
            cp = new ChartPanel(chart);
            cp.setPreferredSize(null);
        }

        // 3) Wrap chart in scroll-pane for BAR/STACKED only
        JComponent chartComp;
        if (STACKED.equals(type) || BAR.equals(type)) {
            chartComp = new JScrollPane(
                    cp,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            );
        } else {
            chartComp = cp;  // direct for pie
        }
        chartComp.setBorder(null);

        // 4) Build fixed-width legend on the right
        JScrollPane legend = buildLegendScroll(chart, counts);
        legend.setPreferredSize(new Dimension(180, legend.getPreferredSize().height));

        // 5) Swap into split pane
        split.setLeftComponent(chartComp);
        split.setRightComponent(legend);
        split.setDividerLocation(0.75);
        split.revalidate();
    }

    private JScrollPane buildLegendScroll(JFreeChart chart, Map<String, Integer> counts) {
        DefaultListModel<String> lm = new DefaultListModel<>();
        counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> lm.addElement(e.getKey() + " (" + e.getValue() + ")"));

        JList<String> list = new JList<>(lm);
        list.setCellRenderer((JList<? extends String> lst, String value, int idx,
                              boolean sel, boolean focus) -> {
            JLabel lbl = new JLabel(value);
            lbl.setOpaque(true);
            lbl.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            if (sel) {
                lbl.setBackground(lst.getSelectionBackground());
                lbl.setForeground(lst.getSelectionForeground());
            }
            // **Colored dots for Pie**: parse the key robustly
            if (chart.getPlot() instanceof PiePlot) {
                int idxParen = value.lastIndexOf(" (");
                String keyName = idxParen > 0 ? value.substring(0, idxParen) : value;
                Paint p = ((PiePlot) chart.getPlot()).getSectionPaint(keyName);
                if (p instanceof Color) {
                    lbl.setIcon(new ColorIcon((Color) p, 10, 10));
                    lbl.setIconTextGap(8);
                }
            }
            return lbl;
        });

        JScrollPane scroll = new JScrollPane(
                list,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(BorderFactory.createTitledBorder("Legend"));
        return scroll;
    }

    private int findColumn(TableModel m, String name) {
        for (int i = 0; i < m.getColumnCount(); i++) {
            if (name.equals(m.getColumnName(i))) return i;
        }
        return -1;
    }

    // Simple colored circle icon for legend dots
    private static class ColorIcon implements Icon {
        private final Color color;
        private final int width, height;
        ColorIcon(Color c, int w, int h) {
            this.color = c;
            this.width = w;
            this.height = h;
        }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(color);
            g.fillOval(x, y, width, height);
        }
        @Override public int getIconWidth()  { return width; }
        @Override public int getIconHeight() { return height; }
    }
}
