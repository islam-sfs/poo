package poo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InventoryUI {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> statusFilter;

    public InventoryUI() {
        initializeUI();
    }

    private void initializeUI() {
        Color background = Color.WHITE;
        Color darkText = Color.decode("#002B5B");
        Color actionBlue = new Color(0, 74, 173);
        Color softBlue = Color.decode("#54A6DF");
        Color darkBackground = new Color(0, 48, 143);
        Color turquoise = new Color(41, 147, 173);
        Color red = new Color(230, 57, 70);

        frame = new JFrame("NextGenStock - Sales Software");
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(background);
        ImageIcon image = new ImageIcon("logo.jpg");
        frame.setIconImage(image.getImage());

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(background);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchField = new JTextField("");
        searchField.setPreferredSize(new Dimension(250, 30));
        searchField.setBackground(Color.white);
        searchField.setForeground(darkText);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        topPanel.add(searchField, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(background);

        String[] statusOptions = {"All", "Existing", "Finish"};
        statusFilter = new JComboBox<>(statusOptions);
        statusFilter.setPreferredSize(new Dimension(120, 30));
        statusFilter.setBackground(Color.WHITE);
        statusFilter.setForeground(darkText);
        rightPanel.add(statusFilter);

        JButton addButton = new JButton("+ add new order");
        addButton.setBackground(actionBlue);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setPreferredSize(new Dimension(150, 30));
        addButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rightPanel.add(addButton);

        topPanel.add(rightPanel, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Product", "Price", "Sub-Family", "Items", "Status"};
        Object[][] data = {};

        model = new DefaultTableModel(data, columnNames);
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(32);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        frame.add(scrollPane, BorderLayout.CENTER);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        statusFilter.addActionListener(e -> {
            String selected = (String) statusFilter.getSelectedItem();
            if (selected.equals("All")) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter(selected, 5));
            }
        });

        table.getColumn("Status").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                String status = value != null ? value.toString() : "";
                JLabel label = new JLabel(status, SwingConstants.CENTER);
                label.setOpaque(true);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                switch (status.toLowerCase()) {
                    case "existing":
                        label.setBackground(turquoise);
                        break;
                    case "finish":
                        label.setBackground(red);
                        break;
                    default:
                        label.setBackground(Color.GRAY);
                }
                return label;
            }
        });

        addButton.addActionListener((ActionEvent e) -> showAddOrderForm(background, turquoise));

        frame.setVisible(true);
    }

    private void showAddOrderForm(Color background, Color turquoise) {
        JFrame addFrame = new JFrame("Add New Order");
        addFrame.setSize(400, 300);
        addFrame.setLayout(new GridLayout(7, 2, 10, 10));
        addFrame.setLocationRelativeTo(null);
        addFrame.getContentPane().setBackground(background);

        JTextField product = new JTextField();
        JTextField category = new JTextField();
        JTextField price = new JTextField();
        JTextField subFamily = new JTextField();
        JTextField items = new JTextField("80/100");
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Existing", "Finish"});

        addFrame.add(new JLabel("Product:")); addFrame.add(product);
        addFrame.add(new JLabel("Category:")); addFrame.add(category);
        addFrame.add(new JLabel("Price:")); addFrame.add(price);
        addFrame.add(new JLabel("Sub-Family:")); addFrame.add(subFamily);
        addFrame.add(new JLabel("Items:")); addFrame.add(items);
        addFrame.add(new JLabel("Status:")); addFrame.add(statusBox);

        JButton save = new JButton("Save");
        save.setBackground(turquoise);
        save.setForeground(Color.WHITE);
        save.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addFrame.add(save);

        save.addActionListener(ev -> {
            model.addRow(new Object[]{
                    "#NEW", product.getText(), price.getText(), subFamily.getText(),
                    items.getText(), statusBox.getSelectedItem()
            });
            addFrame.dispose();
        });

        addFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(InventoryUI::new);
    }
}
