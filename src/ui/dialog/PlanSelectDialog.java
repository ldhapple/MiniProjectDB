package ui.dialog;

import dao.PlanDAO;
import entity.Plan;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class PlanSelectDialog extends JDialog {
    private JTextField phonePlanField, internetPlanField, tvPlanField;
    private JButton phonePlanButton, internetPlanButton, tvPlanButton, selectButton;
    private Plan selectedPhonePlan, selectedInternetPlan, selectedTVPlan;
    private AddCustomerDialog parentDialog;

    public PlanSelectDialog(AddCustomerDialog parentDialog) {
        setTitle("요금제 선택");
        setSize(400, 300);
        setLocationRelativeTo(parentDialog);
        setLayout(new GridLayout(4, 3));

        phonePlanField = new JTextField();
        phonePlanField.setEditable(false);

        internetPlanField = new JTextField();
        internetPlanField.setEditable(false);

        tvPlanField = new JTextField();
        tvPlanField.setEditable(false);

        phonePlanButton = new JButton("핸드폰 요금제");
        internetPlanButton = new JButton("인터넷 요금제");
        tvPlanButton = new JButton("TV 요금제");

        selectButton = new JButton("확인");

        add(new JLabel("핸드폰 요금제:"));
        add(phonePlanField);
        add(phonePlanButton);

        add(new JLabel("인터넷 요금제:"));
        add(internetPlanField);
        add(internetPlanButton);

        add(new JLabel("TV 요금제:"));
        add(tvPlanField);
        add(tvPlanButton);

        add(new JLabel(""));
        add(selectButton);

        phonePlanButton.addActionListener(e -> openPlanSelectDialog("PhonePlan"));
        internetPlanButton.addActionListener(e -> openPlanSelectDialog("InternetPlan"));
        tvPlanButton.addActionListener(e -> openPlanSelectDialog("TVPlan"));

        selectButton.addActionListener(e -> {
            parentDialog.setSelectedPlans(selectedPhonePlan, selectedInternetPlan, selectedTVPlan);
            dispose();
        });
    }

    private void openPlanSelectDialog(String planType) {
        JDialog planSelection = new JDialog(this, "요금제 선택", true);
        planSelection.setSize(600, 400);
        planSelection.setLocationRelativeTo(this);

        JTable planTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DefaultTableModel planTableModel = new DefaultTableModel(new String[]{"ID", "요금제 이름", "요금", "설명"}, 0);
        planTable.setModel(planTableModel);

        PlanDAO planDAO = new PlanDAO();
        List<Plan> plans = planDAO.getAllPlans(planType);
        for (Plan plan : plans) {
            planTableModel.addRow(new Object[]{plan.id(), plan.name(), plan.price(), plan.description()});
        }

        planTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = planTable.getSelectedRow();

                    int planId = (int) planTableModel.getValueAt(selectedRow, 0);
                    String planName = (String) planTableModel.getValueAt(selectedRow, 1);

                    Plan selectedPlan = planDAO.getPlan(planId, planType);

                    switch (planType) {
                        case "PhonePlan":
                            selectedPhonePlan = selectedPlan;
                            phonePlanField.setText(planName);
                            break;
                        case "InternetPlan":
                            selectedInternetPlan = selectedPlan;
                            internetPlanField.setText(planName);
                            break;
                        case "TVPlan":
                            selectedTVPlan = selectedPlan;
                            tvPlanField.setText(planName);
                            break;
                    }

                    planSelection.dispose();
                }
            }
        });

        planSelection.add(new JScrollPane(planTable), BorderLayout.CENTER);
        planSelection.setVisible(true);
    }
}
