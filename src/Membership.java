import java.util.ArrayList;

public class Membership {

    private static final double premiumPrice = 5.00;
    private final ArrayList<Receipts> allPurchases = new ArrayList<Receipts>();
    private boolean isPremium;
    private boolean hasPayed;
    private boolean autoChargeEnabled; // keeps track of payment method
    private String memberFirstName;
    private String memberLastName;
    private String memberEmail;
    private String memberPhone;
    private double totalSpent;

    public Membership(boolean isPremium, boolean hasPayed, boolean autoChargeEnabled, String memberFirstName, String memberLastName, String memberEmail, String memberPhone) {
        this.isPremium = isPremium;
        this.hasPayed = hasPayed;
        this.autoChargeEnabled = autoChargeEnabled;
        this.memberFirstName = memberFirstName;
        this.memberLastName = memberLastName;
        this.memberEmail = memberEmail;
        this.memberPhone = memberPhone;
    }

    public Membership() {
        this.isPremium = false;
        this.hasPayed = false;
        this.autoChargeEnabled = false;
        this.memberFirstName = "Guest";
        this.memberLastName = "Member";
        this.memberEmail = null;
        this.memberPhone = null;
    }

    public Membership(String memberFirstName, String memberLastName, String memberEmail, String memberPhone) {
        this.memberFirstName = memberFirstName;
        this.memberLastName = memberLastName;
        this.memberEmail = memberEmail;
        this.memberPhone = memberPhone;
        this.isPremium = false;
        this.hasPayed = false;
        this.autoChargeEnabled = false;
    }

    public Membership(boolean isPremium, String memberFirstName, String memberLastName, String memberEmail, String memberPhone) {
        this.memberFirstName = memberFirstName;
        this.memberLastName = memberLastName;
        this.memberEmail = memberEmail;
        this.memberPhone = memberPhone;
        this.isPremium = isPremium;
        this.hasPayed = false;
        this.autoChargeEnabled = false;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public boolean hasPayed() {
        return hasPayed;
    }

    public void setHasPayed(boolean hasPayed) {
        this.hasPayed = hasPayed;
    }

    public boolean isAutoChargeEnabled() {
        return autoChargeEnabled;
    }

    public void setAutoChargeEnabled(boolean autoChargeEnabled) {
        this.autoChargeEnabled = autoChargeEnabled;
    }

    public String getMemberFirstName() {
        return memberFirstName;
    }

    public void setMemberFirstName(String memberFirstName) {
        this.memberFirstName = memberFirstName;
    }

    public String getMemberLastName() {
        return memberLastName;
    }

    public void setMemberLastName(String memberLastName) {
        this.memberLastName = memberLastName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getMemberPhone() {
        return memberPhone;
    }

    public void setMemberPhone(String memberPhone) {
        this.memberPhone = memberPhone;
    }

    public void addRecipient(Receipts receipt) {
        allPurchases.add(receipt);
    }

    public void addNewTransaction(double amount) {
        this.totalSpent += amount;
    }

    public String getLastReceipt() {
        return allPurchases.get((allPurchases.size() - 1)).receiptText;
    }


    public double premiumPrice() {
        return premiumPrice;
    }

    public double getTotalSpent() {
        return totalSpent;
    }
}
