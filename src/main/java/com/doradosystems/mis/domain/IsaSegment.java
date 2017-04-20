package com.doradosystems.mis.domain;

public class IsaSegment {
    
    /**
     * An {@link IsaSegment} constructed with blank and default values.
     */
    public static final IsaSegment DEFAULT_VALUES = new IsaSegment("*", "  ", "          ", "  ", "          ", "  ",
            "               ", "  ", "               ", "      ", "    ", " ", "     ", "         ", " ", " ", " ",
            "~");

    private String elementSeparator;
    private String isa01_authorInfoQualifier;
    private String isa02_authorInformation;
    private String isa03_securityInfoQual;
    private String isa04_securityInformation;
    private String isa05_interchangeIdQual;
    private String isa06_interchangeSenderId;
    private String isa07_interchangeIdQual;
    private String isa08_interchangeReceiverId;
    private String isa09_interchangeDate;
    private String isa10_interchangeTime;
    private String isa11_repetitionSeparator;
    private String isa12_interCtrlVersionNum;
    private String isa13_interCtrlNumber;
    private String isa14_ackRequested;
    private String isa15_usageIndicator;
    private String isa16_componentElemSepera;
    private String segmentTerminator;
    
    public IsaSegment(String elementSeparator, String isa01_authorInfoQualifier, String isa02_authorInformation,
            String isa03_securityInfoQual, String isa04_securityInformation, String isa05_interchangeIdQual,
            String isa06_interchangeSenderId, String isa07_interchangeIdQual, String isa08_interchangeReceiverId,
            String isa09_interchangeDate, String isa10_interchangeTime, String isa11_repetitionSeparator,
            String isa12_interCtrlVersionNum, String isa13_interCtrlNumber, String isa14_ackRequested,
            String isa15_usageIndicator, String isa16_componentElemSepera, String segmentTerminator) {
        this.elementSeparator = elementSeparator;
        this.isa01_authorInfoQualifier = isa01_authorInfoQualifier;
        this.isa02_authorInformation = isa02_authorInformation;
        this.isa03_securityInfoQual = isa03_securityInfoQual;
        this.isa04_securityInformation = isa04_securityInformation;
        this.isa05_interchangeIdQual = isa05_interchangeIdQual;
        this.isa06_interchangeSenderId = isa06_interchangeSenderId;
        this.isa07_interchangeIdQual = isa07_interchangeIdQual;
        this.isa08_interchangeReceiverId = isa08_interchangeReceiverId;
        this.isa09_interchangeDate = isa09_interchangeDate;
        this.isa10_interchangeTime = isa10_interchangeTime;
        this.isa11_repetitionSeparator = isa11_repetitionSeparator;
        this.isa12_interCtrlVersionNum = isa12_interCtrlVersionNum;
        this.isa13_interCtrlNumber = isa13_interCtrlNumber;
        this.isa14_ackRequested = isa14_ackRequested;
        this.isa15_usageIndicator = isa15_usageIndicator;
        this.isa16_componentElemSepera = isa16_componentElemSepera;
        this.segmentTerminator = segmentTerminator;
    }

    public String getElementSeparator() {
        return elementSeparator;
    }

    public String getIsa01_authorInfoQualifier() {
        return isa01_authorInfoQualifier;
    }

    public String getIsa02_authorInformation() {
        return isa02_authorInformation;
    }

    public String getIsa03_securityInfoQual() {
        return isa03_securityInfoQual;
    }

    public String getIsa04_securityInformation() {
        return isa04_securityInformation;
    }

    public String getIsa05_interchangeIdQual() {
        return isa05_interchangeIdQual;
    }

    public String getIsa06_interchangeSenderId() {
        return isa06_interchangeSenderId;
    }

    public String getIsa07_interchangeIdQual() {
        return isa07_interchangeIdQual;
    }

    public String getIsa08_interchangeReceiverId() {
        return isa08_interchangeReceiverId;
    }

    public String getIsa09_interchangeDate() {
        return isa09_interchangeDate;
    }

    public String getIsa10_interchangeTime() {
        return isa10_interchangeTime;
    }

    public String getIsa11_repetitionSeparator() {
        return isa11_repetitionSeparator;
    }

    public String getIsa12_interCtrlVersionNum() {
        return isa12_interCtrlVersionNum;
    }

    public String getIsa13_interCtrlNumber() {
        return isa13_interCtrlNumber;
    }

    public String getIsa14_ackRequested() {
        return isa14_ackRequested;
    }

    public String getIsa15_usageIndicator() {
        return isa15_usageIndicator;
    }

    public String getIsa16_componentElemSepera() {
        return isa16_componentElemSepera;
    }

    public String getSegmentTerminator() {
        return segmentTerminator;
    }

    @Override
    public String toString() {
        return "IsaSegment [elementSeparator=" + elementSeparator + ", isa01_authorInfoQualifier="
                + isa01_authorInfoQualifier + ", isa02_authorInformation=" + isa02_authorInformation
                + ", isa03_securityInfoQual=" + isa03_securityInfoQual + ", isa04_securityInformation="
                + isa04_securityInformation + ", isa05_interchangeIdQual=" + isa05_interchangeIdQual
                + ", isa06_interchangeSenderId=" + isa06_interchangeSenderId + ", isa07_interchangeIdQual="
                + isa07_interchangeIdQual + ", isa08_interchangeReceiverId=" + isa08_interchangeReceiverId
                + ", isa09_interchangeDate=" + isa09_interchangeDate + ", isa10_interchangeTime="
                + isa10_interchangeTime + ", isa11_repetitionSeparator=" + isa11_repetitionSeparator
                + ", isa12_interCtrlVersionNum=" + isa12_interCtrlVersionNum + ", isa13_interCtrlNumber="
                + isa13_interCtrlNumber + ", isa14_ackRequested=" + isa14_ackRequested + ", isa15_usageIndicator="
                + isa15_usageIndicator + ", isa16_componentElemSepera=" + isa16_componentElemSepera
                + ", segmentTerminator=" + segmentTerminator + "]";
    }

}
