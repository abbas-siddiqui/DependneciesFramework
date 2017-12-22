/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.HLAData;

/**
 *
 * @author abbas
 */
public class InteractionClassPubSub {
     public String interactionStr;
        private byte[] hndl_value;
        private boolean isPublish;
        private boolean isSubscribe;

    public String getInteractionStr() {
        return interactionStr;
    }

    public void setInteractionStr(String interactionStr) {
        this.interactionStr = interactionStr;
    }

    public byte[] getHndl_value() {
        return hndl_value;
    }

    public void setHndl_value(byte[] hndl_value) {
        this.hndl_value = hndl_value;
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
        

    public InteractionClassPubSub() {
        interactionStr= "";
        isPublish= true;
        isSubscribe= true;
    }

    public InteractionClassPubSub(String interactionStr, byte[] hndl_value, boolean isPublish, boolean isSubscribe) {
        this.interactionStr = interactionStr;
        this.hndl_value = hndl_value;
        this.isPublish = isPublish;
        this.isSubscribe = isSubscribe;
    }
        
        
        
}
