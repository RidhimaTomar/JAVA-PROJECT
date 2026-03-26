package model;

import java.time.LocalDateTime;

public class VerificationSummary {

    private String        docType;
    private String        docNumber;
    private String        holderName;
    private String        status;         
    private String        rejectionReason;
    private LocalDateTime verifiedAt;

    public String        getDocType()                       { return docType; }
    public void          setDocType(String t)               { this.docType = t; }
    public String        getDocNumber()                     { return docNumber; }
    public void          setDocNumber(String n)             { this.docNumber = n; }
    public String        getHolderName()                    { return holderName; }
    public void          setHolderName(String n)            { this.holderName = n; }
    public String        getStatus()                        { return status; }
    public void          setStatus(String s)                { this.status = s; }
    public String        getRejectionReason()               { return rejectionReason; }
    public void          setRejectionReason(String r)       { this.rejectionReason = r; }
    public LocalDateTime getVerifiedAt()                    { return verifiedAt; }
    public void          setVerifiedAt(LocalDateTime t)     { this.verifiedAt = t; }
}
