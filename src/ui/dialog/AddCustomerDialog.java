package ui.dialog;

import dao.CustomerDAO;
import dao.PlanDAO;
import entity.Customer;
import entity.Plan;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import ui.panel.CustomerPanel;

public class AddCustomerDialog extends JDialog {
    private JTextField idField, nameField, ageField, phoneNumberField;
    private JButton addButton;
    private Plan selectedPhonePlan, selectedInternetPlan, selectedTVPlan;
    private CustomerDAO customerDAO = new CustomerDAO();

    public AddCustomerDialog(CustomerPanel parentPanel) {
        setTitle("고객 등록");
        setSize(300, 300);
        setLocationRelativeTo(parentPanel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2));

        idField = new JTextField();
        nameField = new JTextField();
        ageField = new JTextField();
        phoneNumberField = new JTextField();
        JButton planButton = new JButton("요금제 선택");

        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);

        inputPanel.add(new JLabel("이름:"));
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("나이:"));
        inputPanel.add(ageField);

        inputPanel.add(new JLabel("전화번호:"));
        inputPanel.add(phoneNumberField);

        inputPanel.add(new JLabel("요금제:"));
        inputPanel.add(planButton);
        planButton.addActionListener(e -> {
            PlanSelectDialog planSelectDialog = new PlanSelectDialog(this);
            planSelectDialog.setVisible(true);
        });

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("등록");
        buttonPanel.add(addButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            String phoneNumber = phoneNumberField.getText();
            Integer internetPlanId = selectedInternetPlan != null ? selectedInternetPlan.id() : null;
            Integer phonePlanId = selectedPhonePlan != null ? selectedPhonePlan.id() : null;
            Integer tvPlanId = selectedTVPlan != null ? selectedTVPlan.id() : null;
            int gradeId = calculateGrade(internetPlanId, phonePlanId, tvPlanId);

            Customer customer = new Customer(id, name, age, phoneNumber, internetPlanId, phonePlanId, tvPlanId, gradeId);
            customerDAO.insertCustomer(customer);

            dispose();
            parentPanel.refreshCustomerData();
        });
    }

    public void setSelectedPlans(Plan phonePlan, Plan internetPlan, Plan tvPlan) {
        this.selectedPhonePlan = phonePlan;
        this.selectedInternetPlan = internetPlan;
        this.selectedTVPlan = tvPlan;
    }

    private int calculateGrade(Integer internetPlanId, Integer phonePlanId, Integer tvPlanId) {
        PlanDAO planDAO = new PlanDAO();

        int totalCost = 0;

        if (internetPlanId != null) {
            Plan internetPlan = planDAO.getPlan(internetPlanId, "InternetPlan");
            totalCost += internetPlan.price();
        }

        if (phonePlanId != null) {
            Plan phonePlan = planDAO.getPlan(phonePlanId, "PhonePlan");
            totalCost += phonePlan.price();
        }

        if (tvPlanId != null) {
            Plan tvPlan = planDAO.getPlan(tvPlanId, "TVPlan");
            totalCost += tvPlan.price();
        }

        if (totalCost >= 120_000) {
            return 1; //VIP
        } else if (totalCost >= 90_000) {
            return 2; //Diamond
        } else if (totalCost >= 60_000) {
            return 3; //Gold
        } else if (totalCost >= 1){
            return 4; //Basic
        } else {
            return 5; //NoUplus
        }
    }
}
