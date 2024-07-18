package ui.dialog;

import dao.PlanDAO;
import entity.Plan;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import ui.panel.PlanPanel;

public class PlanEditDialog extends JDialog {
    private JTextField idField, nameField, priceField, descriptionField;
    private JButton saveButton;
    private PlanDAO planDAO = new PlanDAO();
    private PlanPanel parentPanel;
    private Plan plan;
    private String currentTable;

    public PlanEditDialog(PlanPanel parentPanel, String title, Plan plan) {
        this.parentPanel = parentPanel;
        this.plan = plan;
        this.currentTable = parentPanel.getCurrentTable();

        setTitle(title);
        setSize(300, 200);
        setLocationRelativeTo(parentPanel);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 2));

        idField = new JTextField();
        nameField = new JTextField();
        priceField = new JTextField();
        descriptionField = new JTextField();
        saveButton = new JButton("저장");

        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("이름:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("요금:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("설명:"));
        inputPanel.add(descriptionField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        if (plan != null) {
            idField.setText(String.valueOf(plan.id()));
            nameField.setText(plan.name());
            priceField.setText(String.valueOf(plan.price()));
            descriptionField.setText(plan.description());
        }

        saveButton.addActionListener(e -> savePlan());
    }

    private void savePlan() {
        int id = Integer.parseInt(idField.getText());
        String name = nameField.getText();
        int price = Integer.parseInt(priceField.getText());
        String description = descriptionField.getText();

        if (plan == null) {
            Plan newPlan = new Plan(id, name, price, description);
            planDAO.insertPlan(newPlan, currentTable);
        } else {
            Plan updatedPlan = new Plan(plan.id(), name, price, description);
            planDAO.updatePlan(updatedPlan, currentTable);
        }

        parentPanel.refreshPlanData();
        dispose();
    }
}
