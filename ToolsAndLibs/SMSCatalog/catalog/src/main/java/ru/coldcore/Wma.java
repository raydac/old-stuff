//#excludeif !SMSENGINE
package ru.coldcore;

public class Wma extends Coordinator
{
    public boolean supportsSMS()
    {
        return true;
    }

    public boolean sendTextSMS(String _smsBody, String _phoneNumber)
    {
        try
        {
            String s_phoneNum = "sms://" + _phoneNumber+":731";
            javax.wireless.messaging.MessageConnection p_connection = (javax.wireless.messaging.MessageConnection) javax.microedition.io.Connector.open(s_phoneNum);
            javax.wireless.messaging.Message p_txtMessage = p_connection.newMessage(javax.wireless.messaging.MessageConnection.TEXT_MESSAGE, s_phoneNum);
            ((javax.wireless.messaging.TextMessage) p_txtMessage).setPayloadText(_smsBody);
            p_connection.send(p_txtMessage);
            p_connection.close();
            p_txtMessage = null;
            p_connection = null;
        }
        catch (Throwable _ex)
        {
            //#-
            _ex.printStackTrace();
            //#+
            return false;
        }
        return true;
    }
}
