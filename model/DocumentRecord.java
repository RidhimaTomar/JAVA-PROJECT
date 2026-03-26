package model;

import java.time.LocalDateTime;

/**
 * A row from the documents table — used for the history/records view.
 */
public class DocumentRecord {

    private int           docId;
    private String        docType;
    private String        docNumber;
    private String        holderName;
    private boolean       locked;
    private String        submittedBy;
    private LocalDateTime submittedAt;

    public int           getDocId()                        { return docId; }
    public void          setDocId(int id)                  { this.docId = id; }
    public String        getDocType()                      { return docType; }
    public void          setDocType(String t)              { this.docType = t; }
    public String        getDocNumber()                    { return docNumber; }
    public void          setDocNumber(String n)            { this.docNumber = n; }
    public String        getHolderName()                   { return holderName; }
    public void          setHolderName(String n)           { this.holderName = n; }
    public boolean       isLocked()                        { return locked; }
    public void          setLocked(boolean l)              { this.locked = l; }
    public String        getSubmittedBy()                  { return submittedBy; }
    public void          setSubmittedBy(String u)          { this.submittedBy = u; }
    public LocalDateTime getSubmittedAt()                  { return submittedAt; }
    public void          setSubmittedAt(LocalDateTime t)   { this.submittedAt = t; }
}
