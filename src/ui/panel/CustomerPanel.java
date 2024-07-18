package ui.panel;

import dao.CustomerDAO;
import dao.GradeDAO;
import entity.Customer;
import entity.Grade;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import ui.dialog.AddCustomerDialog;
import ui.dialog.EditCustomerDialog;
import ui.dialog.ManagePlanDialog;
import ui.dialog.ViewDiscountDialog;

public class CustomerPanel extends JPanel {

    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private CustomerDAO customerDAO = new CustomerDAO();
    private GradeDAO gradeDAO = new GradeDAO();
    private JTextField searchField;
    private JButton searchButton, addButton, editButton, managePlanButton, viewDiscountButton, listButton;

    public CustomerPanel() {
        setLayout(new BorderLayout());

        createSearchPanel();

        String[] columnNames = {"ID", "이름", "나이", "전화번호", "멤버십 등급"};
        customerTableModel = new DefaultTableModel(columnNames, 0);
        customerTable = new JTable(customerTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("고객 등록");
        editButton = new JButton("고객 수정");
        managePlanButton = new JButton("요금제 관리");
        viewDiscountButton = new JButton("할인혜택 확인");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(managePlanButton);
        buttonPanel.add(viewDiscountButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadCustomerData();

        searchButton.addActionListener(e -> {
            String searchWord = searchField.getText();
            if (!searchWord.isBlank()) {
                searchCustomer(searchWord);
            }
        });

        listButton.addActionListener(e -> {
            loadCustomerData();
        });

        addButton.addActionListener(e -> {
            AddCustomerDialog addCustomerDialog = new AddCustomerDialog(this);
            addCustomerDialog.setVisible(true);
        });

        editButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow >= 0) {
                int customerId = (int) customerTableModel.getValueAt(selectedRow, 0);
                EditCustomerDialog editCustomerDialog = new EditCustomerDialog(this, customerId);
                editCustomerDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "고객 선택 후 수정 가능합니다.");
            }
        });

        managePlanButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow >= 0) {
                int customerId = (int) customerTableModel.getValueAt(selectedRow, 0);
                ManagePlanDialog managePlanDialog = new ManagePlanDialog(this, customerId);
                managePlanDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "고객 선택 후 확인 가능합니다.");
            }
        });

        viewDiscountButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow >= 0) {
                int customerId = (int) customerTableModel.getValueAt(selectedRow, 0);
                ViewDiscountDialog viewDiscountDialog = new ViewDiscountDialog(this, customerId);
                viewDiscountDialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "고객 선택 후 확인 가능합니다.");
            }
        });
    }

    private void createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        listButton = new JButton("전체 목록");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(listButton);

        add(searchPanel, BorderLayout.NORTH);
    }

    private void clearCustomer() {
        customerTableModel.setRowCount(0);
    }

    private void loadCustomerData() {
        clearCustomer();

        List<Customer> customers = customerDAO.getAllCustomer();

        for (Customer customer : customers) {
            customerTableModel.addRow(new Object[]{
                    customer.id(),
                    customer.name(),
                    customer.age(),
                    customer.phoneNumber(),
                    gradeDAO.getGradeName(customer.gradeId())
            });
        }
    }

    private void searchCustomer(String name) {
        clearCustomer();

        List<Customer> customers = customerDAO.getCustomersByName(name);

        for (Customer customer : customers) {
            customerTableModel.addRow(new Object[]{
                    customer.id(),
                    customer.name(),
                    customer.age(),
                    customer.phoneNumber(),
                    gradeDAO.getGradeName(customer.gradeId())
            });
        }
    }

    public Customer detailCustomer(int custId) {
        return customerDAO.getCustomer(custId);
    }

    public void updateCustomer(Customer customer) {
        int ret = customerDAO.updateCustomer(customer);
        if (ret == 1) {
            loadCustomerData();
        }
    }

    public void deleteCustomer(int custId) {
        int ret = customerDAO.deleteCustomer(custId);
        if (ret == 1) {
            loadCustomerData();
        }
    }

    public void refreshCustomerData() {
        loadCustomerData();
    }
}
