package model;

/**
 * Customer model
 */
public class Customer {
    private int custId;
    private String custName;
    private String custAddress;
    private String custPostCode;
    private String custPhone;
    private String custDiv;
    private String custCountry;

    /**
     * Customer constructor
     * @param custId
     * @param custName
     * @param custAddress
     * @param custPostCode
     * @param custPhone
     * @param custDivId
     * @param custDiv
     * @param custCountryId
     * @param custCountry
     */
    public Customer(int custId, String custName, String custAddress, String custPostCode, String custPhone, int custDivId, String custDiv, int custCountryId, String custCountry) {
        this.custId = custId;
        this.custName = custName;
        this.custAddress = custAddress;
        this.custPostCode = custPostCode;
        this.custPhone = custPhone;
        this.custDiv = custDiv;
        this.custCountry = custCountry;
    }

    /**
     * Getter to return customer ID.
     * @return custID
     */
    public int getCustId () {
        return custId;
    }

    /**
     * Getter to return the customer name.
     * @return custName
     */
    public String getCustName() {
        return custName;
    }

    /**
     * Getter to return the customer address.
     * @return custAddress
     */
    public String getCustAddress() {
        return custAddress;
    }

    /**
     * Getter to return customer postal code.
     * @return custPostalCode
     */
    public String getCustPostCode() {
        return custPostCode;
    }

    /**
     * Getter to return customer phone number.
     * @return custPhone
     */
    public String getCustPhone() {
        return custPhone;
    }

    /**
     * Getter to return customer division.
     * @return custDiv
     */
    public String getCustDiv() { return custDiv; }

    /**
     * Getter to return customer country.
     * @return custCountry
     */
    public String getCustCountry() {
        return custCountry;
    }

}
