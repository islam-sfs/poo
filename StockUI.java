package poo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StockUI {

    private DefaultTableModel model;
    private JFrame frame;
    private JTable table;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockUI().createAndShowGUI());
    }

    public void createAndShowGUI() {
        frame = new JFrame("In Stock");
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setIconImage(new ImageIcon("logo.png").getImage());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        setupTable(mainPanel);
        setupTopBar(mainPanel);
        setupButtons(mainPanel);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private void setupTable(JPanel parent) {
        String[] columns = {"", "Order ID", "Product", "Category", "Price", "Direction", "Items", "Status"};
        model = new DefaultTableModel(null, columns) {
            public Class<?> getColumnClass(int col) {
                return col == 0 ? Boolean.class : String.class;
            }

            public boolean isCellEditable(int row, int col) {
                return col == 0;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumn("Status").setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel label = new JLabel(value.toString(), SwingConstants.CENTER);
                label.setOpaque(true);
                label.setForeground(Color.WHITE);
                switch (value.toString()) {
                    case "Completed":
                        label.setBackground(new Color(41, 147, 173));
                        break;
                    case "Pending":
                        label.setBackground(new Color(0, 123, 255));
                        break;
                    default:
                        label.setBackground(Color.GRAY);
                }
                return label;
            }
        });

        parent.add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void setupTopBar(JPanel parent) {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);

        JButton newStockBtn = new JButton("+ New Stock");
        newStockBtn.setPreferredSize(new Dimension(120, 30));
        newStockBtn.setBackground(new Color(0, 51, 102));
        newStockBtn.setForeground(Color.WHITE);
        newStockBtn.setFocusPainted(false);

        newStockBtn.addActionListener(e -> showStockForm(false, -1));

        topBar.add(newStockBtn, BorderLayout.EAST);
        parent.add(topBar, BorderLayout.NORTH);
    }

    private void setupButtons(JPanel parent) {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        JButton deleteBtn = new JButton("+ delete");
        JButton editBtn = new JButton("+ edit");

        for (JButton btn : new JButton[]{deleteBtn, editBtn}) {
            btn.setPreferredSize(new Dimension(100, 30));
            btn.setBackground(new Color(0, 51, 102));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            buttonPanel.add(btn);
        }

        deleteBtn.addActionListener(e -> {
            for (int i = model.getRowCount() - 1; i >= 0; i--) {
                if (Boolean.TRUE.equals(model.getValueAt(i, 0))) {
                    model.removeRow(i);
                }
            }
        });

        editBtn.addActionListener(e -> {
            int selectedRow = -1;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (Boolean.TRUE.equals(model.getValueAt(i, 0))) {
                    selectedRow = i;
                    break;
                }
            }
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(frame, "Select one row to edit.");
            } else {
                showStockForm(true, selectedRow);
            }
        });

        parent.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showStockForm(boolean isEdit, int rowIndex) {
        JTextField orderIdField = new JTextField(isEdit ? model.getValueAt(rowIndex, 1).toString() : "#0000");
        JTextField productField = new JTextField(isEdit ? model.getValueAt(rowIndex, 2).toString() : "");
        JTextField categoryField = new JTextField(isEdit ? model.getValueAt(rowIndex, 3).toString() : "");
        JTextField priceField = new JTextField(isEdit ? model.getValueAt(rowIndex, 4).toString() : "");
        JTextField directionField = new JTextField(isEdit ? model.getValueAt(rowIndex, 5).toString() : "");
        JTextField itemsField = new JTextField(isEdit ? model.getValueAt(rowIndex, 6).toString() : "0/100");
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Completed", "Pending"});
        if (isEdit) statusBox.setSelectedItem(model.getValueAt(rowIndex, 7).toString());

        JPanel formPanel = new JPanel(new GridLayout(0, 2));
        formPanel.add(new JLabel("Order ID:")); formPanel.add(orderIdField);
        formPanel.add(new JLabel("Product:")); formPanel.add(productField);
        formPanel.add(new JLabel("Category:")); formPanel.add(categoryField);
        formPanel.add(new JLabel("Price:")); formPanel.add(priceField);
        formPanel.add(new JLabel("Direction:")); formPanel.add(directionField);
        formPanel.add(new JLabel("Items:")); formPanel.add(itemsField);
        formPanel.add(new JLabel("Status:")); formPanel.add(statusBox);

        int result = JOptionPane.showConfirmDialog(null, formPanel,
                isEdit ? "Edit Stock" : "Add New Stock", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Object[] rowData = {
                    false,
                    orderIdField.getText(),
                    productField.getText(),
                    categoryField.getText(),
                    priceField.getText(),
                    directionField.getText(),
                    itemsField.getText(),
                    statusBox.getSelectedItem()
            };

            if (isEdit) {
                for (int i = 0; i < rowData.length; i++) {
                    model.setValueAt(rowData[i], rowIndex, i);
                }
            } else {
                model.addRow(rowData);
            }
        }
    }
}
