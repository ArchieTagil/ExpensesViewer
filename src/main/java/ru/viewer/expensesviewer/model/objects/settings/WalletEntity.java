package ru.viewer.expensesviewer.model.objects.settings;

public class WalletEntity {
    private int walletId;
    private String walletName;
    private double walletBalance;
    private boolean walletDefault;

    public WalletEntity(int walletId, String walletName, double walletBalance, boolean walletDefault) {
        this.walletId = walletId;
        this.walletName = walletName;
        this.walletBalance = walletBalance;
        this.walletDefault = walletDefault;
    }

    @Override
    public String toString() {
        return "WalletEntity{" +
                "walletId=" + walletId +
                ", walletName='" + walletName + '\'' +
                ", walletBalance=" + walletBalance +
                ", walletDefault=" + walletDefault +
                '}';
    }
    @SuppressWarnings("unused")
    public int getWalletId() {
        return walletId;
    }
    @SuppressWarnings("unused")
    public void setWalletId(int walletId) {
        this.walletId = walletId;
    }
    @SuppressWarnings("unused")
    public String getWalletName() {
        return walletName;
    }
    @SuppressWarnings("unused")
    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }
    @SuppressWarnings("unused")
    public double getWalletBalance() {
        return walletBalance;
    }
    @SuppressWarnings("unused")
    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }
    @SuppressWarnings("unused")
    public boolean isWalletDefault() {
        return walletDefault;
    }
    @SuppressWarnings("unused")
    public void setWalletDefault(boolean walletDefault) {
        this.walletDefault = walletDefault;
    }
}
