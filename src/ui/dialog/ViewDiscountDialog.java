package ui.dialog;

import dao.CombineDiscountDAO;
import dao.CustomerDAO;
import dao.DiscountDAO;
import entity.CombineDiscount;
import entity.Customer;
import entity.CustomerCombineDiscount;
import entity.Discount;
import entity.GradeDiscount;
import entity.dto.LifeDiscountInfo;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import ui.panel.CustomerPanel;

public class ViewDiscountDialog extends JDialog {
    private JTabbedPane tabbedPane;
    private DiscountDAO discountDAO = new DiscountDAO();
    private CustomerDAO customerDAO = new CustomerDAO();
    private CombineDiscountDAO combineDiscountDAO = new CombineDiscountDAO();
    private int customerId;

    public ViewDiscountDialog(CustomerPanel parentPanel, int customerId) {
        this.customerId = customerId;

        setTitle("할인 혜택");
        setSize(600, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parentPanel);

        tabbedPane = new JTabbedPane();

        JPanel lifeDiscountPanel = new JPanel(new BorderLayout());
        JTable lifeDiscountTable = new JTable(new DefaultTableModel(new Object[]{"혜택 분야", "혜택 내용", "등급", "할인율"}, 0));
        lifeDiscountPanel.add(new JScrollPane(lifeDiscountTable), BorderLayout.CENTER);
        tabbedPane.addTab("일상생활 할인혜택", lifeDiscountPanel);

        JPanel combineDiscountPanel = new JPanel(new BorderLayout());
        JTable combineDiscountTable = new JTable(new DefaultTableModel(new Object[]{"결합 할인", "할인 금액"}, 0));
        combineDiscountPanel.add(new JScrollPane(combineDiscountTable), BorderLayout.CENTER);
        tabbedPane.addTab("결합할인 혜택", combineDiscountPanel);

        add(tabbedPane, BorderLayout.CENTER);

        loadDiscounts(lifeDiscountTable, combineDiscountTable);
    }

    private void loadDiscounts(JTable lifeDiscountTable, JTable combineDiscountTable) {
        List<LifeDiscountInfo> discounts = discountDAO.getLifestyleDiscountsByCustomerId(customerId);

        DefaultTableModel lifestyleModel = (DefaultTableModel) lifeDiscountTable.getModel();
        for (LifeDiscountInfo discount : discounts) {
            lifestyleModel.addRow(new Object[]{
                    discount.categoryName(),
                    discount.discountDescription(),
                    discount.gradeName(),
                    discount.finalDiscountRate() + "%"
            });
        }

        List<CustomerCombineDiscount> existingCombineDiscounts = combineDiscountDAO.getCustomerCombineDiscounts(customerId);
        List<CombineDiscount> combineDiscounts = combineDiscountDAO.getAllCombineDiscounts();
        Customer customer = customerDAO.getCustomer(customerId);
        int totalAmount = customerDAO.getTotalAmount(customerId);

        DefaultTableModel combineModel = (DefaultTableModel) combineDiscountTable.getModel();

        for (CombineDiscount combineDiscount : combineDiscounts) {
            boolean eligible = true;
            if (combineDiscount.phonePlanRequired() && customer.phonePlanId() == null) {
                eligible = false;
            }
            if (combineDiscount.internetPlanRequired() && customer.internetPlanId() == null) {
                eligible = false;
            }
            if (combineDiscount.tvPlanRequired() && customer.tvPlanId() == null) {
                eligible = false;
            }

            if (eligible && totalAmount >= combineDiscount.minTotalAmount()) {
                boolean exists = existingCombineDiscounts.stream()
                        .anyMatch(cd -> cd.combineDiscountId() == combineDiscount.id());

                if (!exists) {
                    combineDiscountDAO.insertCustomerCombineDiscount(customerId, combineDiscount.id());
                }

                combineModel.addRow(new Object[]{
                        combineDiscount.description(),
                        combineDiscount.discountAmount()
                });
            }
        }
    }
}
