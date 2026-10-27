import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class StockTradingPlatform extends JFrame {
    private final Map<String, Stock> market = new LinkedHashMap<>();
    private final Map<String, Integer> holdings = new LinkedHashMap<>();
    private final Map<String, Double> averageCost = new LinkedHashMap<>();
    private double cash = 100000.00;
    private final DefaultTableModel marketModel = new DefaultTableModel(
        new String[]{"Symbol", "Company", "Price (INR)", "Owned", "Market Value (INR)"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable marketTable = new JTable(marketModel);
    private final JLabel cashLabel = new JLabel();
    private final JLabel portfolioLabel = new JLabel();

    static class Stock {
        String symbol, company;
        double price;
        Stock(String symbol, String company, double price) {
            this.symbol = symbol; this.company = company; this.price = price;
        }
    }

    public StockTradingPlatform() {
        super("CodeAlpha - Stock Trading Platform (Simulation)");
        market.put("TCS", new Stock("TCS", "Tata Consultancy Services", 3500));
        market.put("INFY", new Stock("INFY", "Infosys", 1800));
        market.put("RELIANCE", new Stock("RELIANCE", "Reliance Industries", 2500));
        market.put("WIPRO", new Stock("WIPRO", "Wipro", 550));
        market.put("HDFCBANK", new Stock("HDFCBANK", "HDFC Bank", 1700));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 540);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JLabel heading = new JLabel("STOCK TRADING PLATFORM — PAPER TRADING", SwingConstants.CENTER);
        heading.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        heading.setBorder(BorderFactory.createEmptyBorder(12, 8, 6, 8));
        add(heading, BorderLayout.NORTH);

        marketTable.setRowHeight(26);
        add(new JScrollPane(marketTable), BorderLayout.CENTER);

        JButton buyButton = new JButton("Buy Shares");
        JButton sellButton = new JButton("Sell Shares");
        JButton refreshButton = new JButton("Refresh Portfolio");
        JButton historyButton = new JButton("Transaction History");
        JPanel controls = new JPanel(new FlowLayout());
        controls.add(buyButton); controls.add(sellButton);
        controls.add(refreshButton); controls.add(historyButton);

        JPanel footer = new JPanel(new BorderLayout());
        JPanel summary = new JPanel(new GridLayout(2, 1, 4, 4));
        summary.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        summary.add(cashLabel); summary.add(portfolioLabel);
        footer.add(summary, BorderLayout.NORTH);
        footer.add(controls, BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);

        buyButton.addActionListener(e -> trade(true));
        sellButton.addActionListener(e -> trade(false));
        refreshButton.addActionListener(e -> refresh());
        historyButton.addActionListener(e -> showHistory());

        refresh();
    }

    private final StringBuilder history = new StringBuilder();

    private void trade(boolean buy) {
        int row = marketTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a stock from the market table first.");
            return;
        }
        String symbol = (String) marketModel.getValueAt(marketTable.convertRowIndexToModel(row), 0);
        Stock stock = market.get(symbol);
        String value = JOptionPane.showInputDialog(this, "Enter quantity for " + symbol
            + " (price: Rs. " + String.format("%.2f", stock.price) + "):");
        if (value == null) return;

        try {
            int quantity = Integer.parseInt(value.trim());
            if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive.");
            double amount = stock.price * quantity;

            if (buy) {
                if (amount > cash) throw new IllegalArgumentException("Insufficient cash balance.");
                int oldQty = holdings.getOrDefault(symbol, 0);
                double oldCost = averageCost.getOrDefault(symbol, 0.0) * oldQty;
                holdings.put(symbol, oldQty + quantity);
                averageCost.put(symbol, (oldCost + amount) / (oldQty + quantity));
                cash -= amount;
                history.append("BUY ").append(quantity).append(" ").append(symbol)
                    .append(" @ Rs. ").append(String.format("%.2f", stock.price))
                    .append(" | Total Rs. ").append(String.format("%.2f", amount)).append("\\n");
            } else {
                int owned = holdings.getOrDefault(symbol, 0);
                if (quantity > owned) throw new IllegalArgumentException("You only own " + owned + " shares.");
                cash += amount;
                int remaining = owned - quantity;
                if (remaining == 0) {
                    holdings.remove(symbol);
                    averageCost.remove(symbol);
                } else {
                    holdings.put(symbol, remaining);
                }
                history.append("SELL ").append(quantity).append(" ").append(symbol)
                    .append(" @ Rs. ").append(String.format("%.2f", stock.price))
                    .append(" | Total Rs. ").append(String.format("%.2f", amount)).append("\\n");
            }

            refresh();
            JOptionPane.showMessageDialog(this, (buy ? "Purchase" : "Sale")
                + " completed. Transaction value: Rs. " + String.format("%.2f", amount));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a whole-number quantity.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Transaction Failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refresh() {
        marketModel.setRowCount(0);
        double marketValue = 0, costBasis = 0;
        for (Stock s : market.values()) {
            int qty = holdings.getOrDefault(s.symbol, 0);
            double value = qty * s.price;
            marketValue += value;
            costBasis += qty * averageCost.getOrDefault(s.symbol, 0.0);
            marketModel.addRow(new Object[]{s.symbol, s.company, String.format("%.2f", s.price),
                qty, String.format("%.2f", value)});
        }
        cashLabel.setText(String.format("Available cash: Rs. %.2f", cash));
        portfolioLabel.setText(String.format(
            "Stock value: Rs. %.2f | Total portfolio: Rs. %.2f | Unrealized P/L: Rs. %.2f",
            marketValue, cash + marketValue, marketValue - costBasis));
    }

    private void showHistory() {
        JTextArea area = new JTextArea(history.length() == 0 ? "No transactions yet." : history.toString(), 16, 55);
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Transaction History", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StockTradingPlatform().setVisible(true));
    }
}
