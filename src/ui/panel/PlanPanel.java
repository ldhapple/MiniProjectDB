package ui.panel;

import dao.PlanDAO;
import entity.Plan;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import ui.dialog.PlanEditDialog;

public class PlanPanel extends JPanel {
    private JTable planTable;
    private DefaultTableModel planTableModel;
    private PlanDAO planDAO = new PlanDAO();
    private String currentTable;

    public PlanPanel() {
        setLayout(new BorderLayout());

        // Create MenuBar
        JMenuBar menuBar = new JMenuBar();
        JMenuItem phonePlanMenuItem = new JMenuItem("핸드폰 요금제");
        JMenuItem tvPlanMenuItem = new JMenuItem("TV 요금제");
        JMenuItem internetPlanMenuItem = new JMenuItem("인터넷 요금제");
        menuBar.add(phonePlanMenuItem);
        menuBar.add(tvPlanMenuItem);
        menuBar.add(internetPlanMenuItem);
        add(menuBar, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Name", "Price", "Description"};
        planTableModel = new DefaultTableModel(columnNames, 0);
        planTable = new JTable(planTableModel);
        JScrollPane scrollPane = new JScrollPane(planTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("추가");
        JButton editButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        phonePlanMenuItem.addActionListener(e -> loadPlanData("PhonePlan"));
        tvPlanMenuItem.addActionListener(e -> loadPlanData("TVPlan"));
        internetPlanMenuItem.addActionListener(e -> loadPlanData("InternetPlan"));

        addButton.addActionListener(e -> addPlan());
        editButton.addActionListener(e -> editPlan());
        deleteButton.addActionListener(e -> deletePlan());

        loadPlanData("PhonePlan");
    }

    public String getCurrentTable() {
        return currentTable;
    }

    public void refreshPlanData() {
        loadPlanData(currentTable);
    }

    private void loadPlanData(String tableName) {
        currentTable = tableName;

        planTableModel.setRowCount(0);

        List<Plan> plans = planDAO.getAllPlans(tableName);
        for (Plan plan : plans) {
            planTableModel.addRow(new Object[]{
                    plan.id(),
                    plan.name(),
                    plan.price(),
                    plan.description()
            });
        }
    }

    private void addPlan() {
        PlanEditDialog planEditDialog = new PlanEditDialog(this, "요금제 등록", null);
        planEditDialog.setVisible(true);
    }

    private void editPlan() {
        int selectedRow = planTable.getSelectedRow();
        if (selectedRow >= 0) {
            int planId = (int) planTableModel.getValueAt(selectedRow, 0);
            String planName = (String) planTableModel.getValueAt(selectedRow, 1);
            int planPrice = (int) planTableModel.getValueAt(selectedRow, 2);
            String planDescription = (String) planTableModel.getValueAt(selectedRow, 3);

            Plan plan = new Plan(planId, planName, planPrice, planDescription);
            PlanEditDialog planEditDialog = new PlanEditDialog(this, "요금제 수정", plan);
            planEditDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "수정할 요금제를 선택해주세요.");
        }
    }

    private void deletePlan() {
        int selectedRow = planTable.getSelectedRow();
        if (selectedRow >= 0) {
            int planId = (int) planTableModel.getValueAt(selectedRow, 0);
            int result = planDAO.deletePlan(planId, currentTable);
            if (result == 1) {
                loadPlanData(currentTable);
            }
        } else {
            JOptionPane.showMessageDialog(this, "삭제할 요금제를 선택해주세요.");
        }
    }
}
