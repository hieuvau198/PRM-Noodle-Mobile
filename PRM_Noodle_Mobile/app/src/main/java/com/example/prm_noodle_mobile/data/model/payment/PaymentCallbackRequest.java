package com.example.prm_noodle_mobile.data.model.payment;

public class PaymentCallbackRequest {
    private String vnp_TxnRef;
    private String vnp_ResponseCode;
    private String vnp_TransactionStatus;
    private String vnp_Amount;
    private String vnp_BankCode;
    private String vnp_PayDate;
    private String vnp_TransactionNo;
    private String vnp_SecureHash;

    public String getVnp_TxnRef() { return vnp_TxnRef; }
    public void setVnp_TxnRef(String vnp_TxnRef) { this.vnp_TxnRef = vnp_TxnRef; }
    public String getVnp_ResponseCode() { return vnp_ResponseCode; }
    public void setVnp_ResponseCode(String vnp_ResponseCode) { this.vnp_ResponseCode = vnp_ResponseCode; }
    public String getVnp_TransactionStatus() { return vnp_TransactionStatus; }
    public void setVnp_TransactionStatus(String vnp_TransactionStatus) { this.vnp_TransactionStatus = vnp_TransactionStatus; }
    public String getVnp_Amount() { return vnp_Amount; }
    public void setVnp_Amount(String vnp_Amount) { this.vnp_Amount = vnp_Amount; }
    public String getVnp_BankCode() { return vnp_BankCode; }
    public void setVnp_BankCode(String vnp_BankCode) { this.vnp_BankCode = vnp_BankCode; }
    public String getVnp_PayDate() { return vnp_PayDate; }
    public void setVnp_PayDate(String vnp_PayDate) { this.vnp_PayDate = vnp_PayDate; }
    public String getVnp_TransactionNo() { return vnp_TransactionNo; }
    public void setVnp_TransactionNo(String vnp_TransactionNo) { this.vnp_TransactionNo = vnp_TransactionNo; }
    public String getVnp_SecureHash() { return vnp_SecureHash; }
    public void setVnp_SecureHash(String vnp_SecureHash) { this.vnp_SecureHash = vnp_SecureHash; }
}
