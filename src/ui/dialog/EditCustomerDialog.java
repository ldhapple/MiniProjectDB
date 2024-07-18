package ui.dialog;

import dao.CustomerDAO;
import entity.Customer;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import ui.panel.CustomerPanel;

public class EditCustomerDialog extends JDialog {
    private JTextField nameField, ageField, phoneNumberField;
    private JButton applyButton, deleteButton;

    public EditCustomerDialog(CustomerPanel parentPanel, int customerId) {
        Customer customer = parentPanel.detailCustomer(customerId);
        setTitle("고객 정보 수정");
        setSize(400, 300);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));
        setLocationRelativeTo(parentPanel);

        inputPanel.add(new JLabel("이름:"));
        nameField = new JTextField(customer.name());
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("나이:"));
        ageField = new JTextField(String.valueOf(customer.age()));
        inputPanel.add(ageField);

        inputPanel.add(new JLabel("전화번호:"));
        phoneNumberField = new JTextField(customer.phoneNumber());
        inputPanel.add(phoneNumberField);

        JPanel buttonPanel = new JPanel();
        applyButton = new JButton("수정");
        applyButton.addActionListener(e -> {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String phoneNumber = phoneNumberField.getText();

            parentPanel.updateCustomer(
                    new Customer(customerId, name, age, phoneNumber, customer.internetPlanId(), customer.phonePlanId(),
                            customer.tvPlanId(), customer.gradeId()));
            dispose();
            parentPanel.refreshCustomerData();
        });
        buttonPanel.add(applyButton);

        deleteButton = new JButton("삭제");
        deleteButton.addActionListener(e -> {
            parentPanel.deleteCustomer(customerId);
            dispose();
            parentPanel.refreshCustomerData();
        });
        buttonPanel.add(deleteButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
