import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class NextGenStockUI extends JFrame {
    JLabel totalLabel, dateTimeLabel, ticketLabel;
    JTable productTable;
    DefaultTableModel tableModel;
    HashMap<String, Double> productPrices;
    double totalAmount = 0.0;
    int ticketCounter = 1;
    DecimalFormat df = new DecimalFormat("000");

    CardLayout cardLayout;
    JPanel mainPanel;
    JPanel welcomePanel;
    JPanel leftPanel;

    public NextGenStockUI() {
        setTitle("NextGenStock");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        welcomePanel = createWelcomePanel();
        mainPanel.add(welcomePanel, "welcome");
        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createMainAppPanel(), "app");

        add(mainPanel);
        cardLayout.show(mainPanel, "welcome");

        setVisible(true);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(null);
        panel.setPreferredSize(new Dimension(1200, 800));
        panel.setBackground(Color.WHITE);

        JLabel welcomeTitle = new JLabel("Welcome to NextGenStock!");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeTitle.setForeground(new Color(25, 25, 112));
        welcomeTitle.setBounds(350, 250, 600, 50);

        JButton startButton = new JButton("Start");
        startButton.setBackground(new Color(34, 139, 34));
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        startButton.setFocusPainted(false);
        startButton.setBounds(525, 350, 150, 50);

        startButton.addActionListener(e -> {
            Timer timer = new Timer(10, null);
            final int[] step = {0};

            timer.addActionListener(evt -> {
                if (step[0] >= 30) {
                    timer.stop();
                    cardLayout.show(mainPanel, "login");
                    return;
                }
                panel.setLocation(0, -step[0] * 10);
                step[0]++;
            });
            timer.start();
        });

        panel.add(welcomeTitle);
        panel.add(startButton);

        return panel;
    }
    public void addProductFromInventory(String name, double price) {
        productPrices.put(name, price);
        refreshProductButtons(); // حتى يظهر في الأزرار الجانبية
        addProductToTable(name); // حتى ينضاف مباشرة في الجدول
    }
    

    private JPanel createLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("NextGenStock Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(25, 25, 112));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(10,10,20,10);
        loginPanel.add(title, gbc);

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);
        gbc.gridwidth = 1; gbc.gridy++;
        loginPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(15);
        gbc.gridx = 0; gbc.gridy++;
        loginPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        loginPanel.add(passField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(34, 139, 34));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            if (user.equals("islam") && pass.equals("1234")) {
                cardLayout.show(mainPanel, "app");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.insets = new Insets(20,10,10,10);
        loginPanel.add(loginButton, gbc);

        return loginPanel;
    }
    

    private JPanel createMainAppPanel() {
        JPanel appPanel = new JPanel(new BorderLayout());
        initializeProductPrices();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(25, 25, 112));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel title = new JLabel("NextGenStock", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        ticketLabel = new JLabel("Ticket: TICKET-" + df.format(ticketCounter));
        ticketLabel.setForeground(Color.YELLOW);
        ticketLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(ticketLabel, BorderLayout.EAST);
        appPanel.add(topPanel, BorderLayout.NORTH);

        leftPanel = new JPanel(new GridLayout(16, 1, 12, 12));
        leftPanel.setBackground(new Color(245, 245, 245));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        refreshProductButtons();

        JButton addProductBtn = createStyledButton("+ Add Product", new Color(50, 205, 50), Color.BLACK);
        addProductBtn.addActionListener(e -> new InventoryUI(this));



        appPanel.add(leftPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        totalLabel = new JLabel("00,00 DA", SwingConstants.CENTER);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        totalLabel.setForeground(new Color(25, 25, 112));
        centerPanel.add(totalLabel, BorderLayout.NORTH);

        String[] columns = {"Product", "Barcode", "Unit Price", "Qty", "Total"};
        tableModel = new DefaultTableModel(columns, 0);
        productTable = new JTable(tableModel);
        productTable.setRowHeight(28);
        productTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        centerPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        appPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(250, 250, 250));
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        rightPanel.add(addProductBtn);
        dateTimeLabel = new JLabel();
        updateTime();
        dateTimeLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        dateTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(dateTimeLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        String[] actions = { "Modify Price", "Modify Quantity", "Ticket","Delete", "Print Invoice", "Search Product", "Return"};
        for (String action : actions) {
            JButton btn = createStyledButton(action, new Color(100, 149, 237), Color.WHITE);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(180, 60));
            if (action.equals("Print Invoice")) {
                btn.addActionListener(this::saveInvoiceToFile);
            } else if (action.equals("Delete")) {
                btn.addActionListener(this::deleteSelectedRow);
            } else if (action.equals("Ticket")) {
                btn.addActionListener(this::showTicket);
            } else if (action.equals("Search Product")) {
                btn.addActionListener(this::searchProduct);
            } else if (action.equals("Modify Price")) {
                btn.addActionListener(this::modifyPrice);
            } else if (action.equals("Modify Quantity")) {
                btn.addActionListener(this::modifyQuantity);
            }
            
            rightPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            
            rightPanel.add(btn);
            
        }
        appPanel.add(rightPanel, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));

        JButton taxBtn = createStyledButton("Show Tax", new Color(255, 140, 0), Color.WHITE);
        taxBtn.addActionListener(this::showTax);
        JButton cancelBtn = createStyledButton("Cancel", Color.GRAY, Color.WHITE);
        cancelBtn.addActionListener(e -> clearAll());
        JButton payBtn = createStyledButton("Pay", new Color(34, 139, 34), Color.white);
        payBtn.addActionListener(this::processPayment);
        payBtn.addActionListener(e -> clearAll());

        bottomPanel.add(taxBtn);
        bottomPanel.add(cancelBtn);
        bottomPanel.add(payBtn);
        bottomPanel.add(addProductBtn);
        appPanel.add(bottomPanel, BorderLayout.SOUTH);
        return appPanel;
    }

    private void showAddProductDialog() {
        JTextField nameField = new JTextField(10);
        JTextField priceField = new JTextField(5);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Name: "));
        panel.add(nameField);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(new JLabel("Price: "));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                if (!name.isEmpty()) {
                    productPrices.put(name, price);
                    refreshProductButtons();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price.");
            }
        }
    }

    private void refreshProductButtons() {
        leftPanel.removeAll();
        
        for (String product : productPrices.keySet()) {
            JButton productButton = createStyledButton(product, Color.CYAN, Color.BLACK);
            productButton.addActionListener(e -> addProductToTable(product));
            leftPanel.add(productButton); }

        leftPanel.revalidate();
        leftPanel.repaint();
        
        
    }

    private void initializeProductPrices() {
        productPrices = new HashMap<>();
        productPrices.put("milk", 150.0);
        productPrices.put("olive", 300.0);
        productPrices.put("cheese", 170.0);
        productPrices.put("coca", 100.0);
        productPrices.put("bean", 250.0);
        productPrices.put("egg", 30.0);
        productPrices.put("flour", 80.0);
        
        productPrices.put("choclate", 150.0);
        
        productPrices.put("bread", 150.0);
        
        productPrices.put("suger", 150.0);
        
        productPrices.put("salt", 150.0);
        
        productPrices.put("meat", 150.0);

    }


    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setFocusPainted(false);
        return button;
    }

    private void updateTime() {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
            dateTimeLabel.setText(dateFormat.format(new Date()));
        });
        timer.start();
    }

    private void addProductToTable(String product) {
        double price = productPrices.get(product);
        tableModel.addRow(new Object[]{product, "000" + (int) (Math.random() * 1000), price, 1, price});
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        totalAmount = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            totalAmount += (double) tableModel.getValueAt(i, 4);
        }
        updateTotalLabel();
    }

    private void updateTotalLabel() {
        totalLabel.setText(String.format("%.2f DA", totalAmount));
    }

    private void saveInvoiceToFile(ActionEvent e) {
        try {
            File file = new File("invoice_ticket_" + df.format(ticketCounter) + ".txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("Invoice - Ticket: TICKET-" + df.format(ticketCounter) + "\n\n");
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.write(tableModel.getValueAt(i, 0) + " x " + tableModel.getValueAt(i, 3) + " = "
                        + tableModel.getValueAt(i, 4) + " DA\n");
            }
            writer.write("\nTotal: " + totalAmount + " DA");
            writer.close();
            JOptionPane.showMessageDialog(this, "Invoice saved successfully!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteSelectedRow(ActionEvent e) {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
            updateTotalAmount();
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
        }
    }

    private void showTicket(ActionEvent e) {
        JOptionPane.showMessageDialog(this, "Ticket Number: TICKET-" + df.format(ticketCounter));
    }

    private void searchProduct(ActionEvent e) {
        String search = JOptionPane.showInputDialog(this, "Enter product name:");
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(search)) {
                productTable.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    private void modifyPrice(ActionEvent e) {
        int row = productTable.getSelectedRow();
        if (row >= 0) {
            String newPrice = JOptionPane.showInputDialog(this, "Enter new price:");
            try {
                double price = Double.parseDouble(newPrice);
                tableModel.setValueAt(price, row, 2);
                tableModel.setValueAt(price * (int) tableModel.getValueAt(row, 3), row, 4);
                updateTotalAmount();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price.");
            }
        }
    }

    private void modifyQuantity(ActionEvent e) {
        int row = productTable.getSelectedRow();
        if (row >= 0) {
            String newQuantity = JOptionPane.showInputDialog(this, "Enter new quantity:");
            try {
                int qty = Integer.parseInt(newQuantity);
                tableModel.setValueAt(qty, row, 3);
                tableModel.setValueAt((double) tableModel.getValueAt(row, 2) * qty, row, 4);
                updateTotalAmount();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid quantity.");
            }
        }
    }

    private void showTax(ActionEvent e) {
        JOptionPane.showMessageDialog(this, "Tax applied: " + (totalAmount * 0.2));
    }

    private void processPayment(ActionEvent e) {
        JOptionPane.showMessageDialog(this, "Payment successful! Total: " + totalAmount + " DA");
    }

    private void clearAll() {
        tableModel.setRowCount(0);
        totalAmount = 0;
        updateTotalAmount();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NextGenStockUI());
    }
}
