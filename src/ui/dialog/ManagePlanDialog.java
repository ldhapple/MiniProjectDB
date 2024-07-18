package ui.dialog;

import dao.CombineDiscountDAO;
import dao.CustomerDAO;
import dao.PlanDAO;
import entity.CombineDiscount;
import entity.Customer;
import entity.Plan;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import ui.panel.CustomerPanel;

public class ManagePlanDialog extends JDialog {
    private JTable planTable;
    private DefaultTableModel planTableModel;
    private CustomerDAO customerDAO = new CustomerDAO();
    private CombineDiscountDAO combineDiscountDAO = new CombineDiscountDAO();
    private PlanDAO planDAO = new PlanDAO();

    public ManagePlanDialog(CustomerPanel parentPanel, int customerId) {
        setTitle("요금제 관리");
        setSize(600, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parentPanel);

        String[] columnNames = {"기기 종류", "요금제 이름", "요금", "설명"};
        planTableModel = new DefaultTableModel(columnNames, 0);
        planTable = new JTable(planTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JScrollPane scrollPane = new JScrollPane(planTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton applyButton = new JButton("수정");
        applyButton.addActionListener(e -> {
            updateCustomerPlans(customerId);
            dispose();
            parentPanel.refreshCustomerData();
        });
        add(applyButton, BorderLayout.SOUTH);

        loadCustomerPlans(customerId);
    }

    private void loadCustomerPlans(int customerId) {
        Customer customer = customerDAO.getCustomer(customerId);

        addPlanRow("PhonePlan", customer.phonePlanId());
        addPlanRow("InternetPlan", customer.internetPlanId());
        addPlanRow("TVPlan", customer.tvPlanId());

        planTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = planTable.getSelectedRow();
                    String planType = (String) planTableModel.getValueAt(selectedRow, 0);
                    openPlanSelectDialog(planType, selectedRow);
                }
            }
        });
    }

    private void addPlanRow(String planType, Integer planId) {
        if (planId != null) {
            Plan plan = planDAO.getPlanById(planId, planType);
            if (plan != null) {
                planTableModel.addRow(new Object[]{planType, plan.name(), plan.price(), plan.description()});
            }
        } else {
            planTableModel.addRow(new Object[]{planType, "", "", ""});
        }
    }

    private void openPlanSelectDialog(String planType, int rowIndex) {
        JDialog planSelection = new JDialog(this, "요금제 선택", true);
        planSelection.setSize(600, 400);
        planSelection.setLocationRelativeTo(this);

        JTable planTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        DefaultTableModel totalPlanTableModel = new DefaultTableModel(new String[]{"ID", "요금제 이름", "요금", "설명"}, 0);
        planTable.setModel(totalPlanTableModel);

        List<Plan> plans = planDAO.getAllPlans(planType);
        for (Plan plan : plans) {
            totalPlanTableModel.addRow(new Object[]{plan.id(), plan.name(), plan.price(), plan.description()});
        }

        planTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = planTable.getSelectedRow();

                    int planId = (int) totalPlanTableModel.getValueAt(selectedRow, 0);
                    String planName = (String) totalPlanTableModel.getValueAt(selectedRow, 1);
                    int planPrice = (int) totalPlanTableModel.getValueAt(selectedRow, 2);
                    String planDescription = (String) totalPlanTableModel.getValueAt(selectedRow, 3);

                    planTableModel.setValueAt(planName, rowIndex, 1);
                    planTableModel.setValueAt(planPrice, rowIndex, 2);
                    planTableModel.setValueAt(planDescription, rowIndex, 3);

                    planSelection.dispose();
                }
            }
        });

        planSelection.add(new JScrollPane(planTable), BorderLayout.CENTER);
        planSelection.setVisible(true);
    }

    private int calculateGrade(Customer customer) {
        int totalCost = 0;
        if (customer.internetPlanId() != null) {
            Plan internetPlan = planDAO.getPlanById(customer.internetPlanId(), "InternetPlan");
            totalCost += internetPlan.price();
        }
        if (customer.phonePlanId() != null) {
            Plan phonePlan = planDAO.getPlanById(customer.phonePlanId(), "PhonePlan");
            totalCost += phonePlan.price();
        }
        if (customer.tvPlanId() != null) {
            Plan tvPlan = planDAO.getPlanById(customer.tvPlanId(), "TVPlan");
            totalCost += tvPlan.price();
        }

        if (totalCost >= 120_000) {
            return 1; //VIP - 10%
        } else if (totalCost >= 90_000) {
            return 2; //Diamond - 8%
        } else if (totalCost >= 60_000) {
            return 3; //Gold - 6%
        } else if (totalCost >= 1){
            return 4; //Basic - 1%
        } else {
            return 5; //NoUplus - 0%
        }
    }

    private void updateCustomerPlans(int customerId) {
        for (int row = 0; row < planTableModel.getRowCount(); row++) {
            String planType = (String) planTableModel.getValueAt(row, 0);
            String planName = (String) planTableModel.getValueAt(row, 1);

            if (!planName.isEmpty()) {
                Plan plan = planDAO.getPlanByName(planName, planType);
                if (plan != null) {
                    customerDAO.updateCustomerPlan(customerId, planType, plan.id());
                }
            } else {
                customerDAO.updateCustomerPlan(customerId, planType, null);
            }
        }

        Customer customer = customerDAO.getCustomer(customerId);
        int gradeId = calculateGrade(customer);
        customerDAO.updateCustomerGrade(customerId, gradeId);
        updateCustomerCombineDiscounts(customerId);
    }

    private void updateCustomerCombineDiscounts(int customerId) {
        List<CombineDiscount> combineDiscounts = combineDiscountDAO.getAllCombineDiscounts();
        int totalAmount = customerDAO.getTotalAmount(customerId);

        for (CombineDiscount combineDiscount : combineDiscounts) {
            boolean eligible = true;
            if (combineDiscount.phonePlanRequired() && customerDAO.getCustomer(customerId).phonePlanId() == null) {
                eligible = false;
            }
            if (combineDiscount.internetPlanRequired() && customerDAO.getCustomer(customerId).internetPlanId() == null) {
                eligible = false;
            }
            if (combineDiscount.tvPlanRequired() && customerDAO.getCustomer(customerId).tvPlanId() == null) {
                eligible = false;
            }
            if (eligible && totalAmount >= combineDiscount.minTotalAmount()) {
                combineDiscountDAO.insertOrUpdateCustomerCombineDiscount(customerId, combineDiscount.id());
            }
        }
    }
}
