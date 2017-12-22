/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Data.HLAMessages;

/**
 *
 * @author abbas
 */
public class FederationGenericMsg<T> {

    private String type;
    private String federationName; // Optional if only single Federation
    private String federateName; // Which Federate Generate this Message --- Optional
    private T objectAttached; // Optional

    public String getFederationName() {
        return federationName;
    }

    public void setFederationName(String federationName) {
        this.federationName = federationName;
    }

    public String getFederateName() {
        return federateName;
    }

    public void setFederateName(String federateName) {
        this.federateName = federateName;
    }


    public String getType() {
        return type;
          

    }

    public void setType(String type) {
        this.type = type;
    }

    public T getData() {
        return objectAttached;
    }

    public void setData(T data) {
        this.objectAttached = data;
    }
    
    public FederationGenericMsg() {
        setDefaults();
    }
  private void setDefaults(){
  
      type= "none";
      objectAttached = null;
      federationName ="federation";
      federateName= "federate";
  }
  
//  Type getType(Class<?> rawClass, Class<?> parameter) {
//
//  return new ParametrizedType() {
//
//    @Override
//
//    public Type[] getActualTypeArguments() {
//
//       return new Type[] {parameter};
//
//    }
//
//    @Override
//
//    public Type getRawType() {
//
//      return rawClass;
//
//    }
//
//    @Override
//
//    public Type getOwnerType() {
//
//      return null;
//
//    }   
//
//  }


    
    
}
