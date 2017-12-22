/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.HLAData;

import CommonFnc.StaticFunctions;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.RTIinternalError;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author abbas
 */
public class PubSubAttribute {

    private String attributestr;
    private byte[] hndl_value;
    private String type ;
    private boolean isPublish;
    private boolean isSubscribe;
    private AttributeHandle attrHndl;

    public String getAttributestr() {
        return attributestr;
    }

    public void setAttributestr(String attributestr) {
        this.attributestr = attributestr;
    }

    public byte[] getHndl_value() {
        return hndl_value;
    }

    public void setHndl_value(byte[] hndl_value) {
        this.hndl_value = hndl_value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsPublish() {
        return isPublish;
    }

    public void setIsPublish(boolean isPublish) {
        this.isPublish = isPublish;
    }

    public boolean isIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(boolean isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public AttributeHandle getAttrHndl() {
        return attrHndl;
    }

    public void setAttrHndl(AttributeHandle attrHndl) {
        this.attrHndl = attrHndl;
    }
    

    public PubSubAttribute(String typea) {
        this.type = typea;
    }

    public PubSubAttribute() {

    }
    
//    public AttributeHandle getAttributeHandle(RTIambassador rtiamb)
//    {
//    rtiamb.getAttributeHandle(sodaHandle, "NumberCups");
//    }

    public void SetValue(String val) {
        try {
            EncoderFactory encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

            switch (type) {
                case "HLAinteger32BE":
                    byte[] valb = StaticFunctions.toByteArray(Integer.valueOf(val));
                    HLAinteger32BE val_p = encoderFactory.createHLAinteger32BE(ByteBuffer.wrap(valb).getInt());
                    hndl_value = val_p.toByteArray();

                    break;
                case "UnsignedShort":
                    break;
                case "HLAASCIIstring":
                    HLAASCIIstring val_s = encoderFactory.createHLAASCIIstring(val);
                    hndl_value = val_s.toByteArray();
                    break;

            }

        } catch (RTIinternalError ex) {
            Logger.getLogger(PubSubAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public byte[] GetValue() {

        return hndl_value;
    }

    @Override
    public String toString() {
        return attributestr;
    }
}
