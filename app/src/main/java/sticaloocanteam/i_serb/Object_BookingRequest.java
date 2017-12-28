package sticaloocanteam.i_serb;

import android.content.Context;

/**
 * Created by kim on 12/3/2017.
 */

public class Object_BookingRequest {


    String serviceid;
    String serviceDescription;
    String reqDateTime;
    String customerName;

    public Object_BookingRequest(String serviceid, String serviceDescription, String reqDateTime, String customerName) {
        this.serviceid = serviceid;
        this.serviceDescription = serviceDescription;
        this.reqDateTime = reqDateTime;
        this.customerName = customerName;
    }

    public String getServiceid() {
        return serviceid;
    }

    public void setServiceid(String serviceid) {
        this.serviceid = serviceid;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getReqDateTime() {
        return reqDateTime;
    }

    public void setReqDateTime(String reqDateTime) {
        this.reqDateTime = reqDateTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
