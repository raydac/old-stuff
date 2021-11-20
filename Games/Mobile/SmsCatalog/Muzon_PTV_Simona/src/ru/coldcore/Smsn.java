//#excludeif !SMSENGINE
package ru.coldcore;

public class Smsn extends Coordinator
{
    public boolean sendTextSMS(String _smsBody,String _phoneNumber)
    {
    try
    {
           _smsBody = '\n'+_smsBody+'\n';
           //$com.samsung.util.SM p_sms = new com.samsung.util.SM(_phoneNumber,"+78120000000",_smsBody);
           //$com.samsung.util.SMS.send(p_sms);
           //$p_sms = null;
     }catch(Throwable _ex)
    {
        //#-
        _ex.printStackTrace();
        //#+
        return false;
    }
    return true;
    }

    public boolean supportsSMS()
    {
        //$return com.samsung.util.SMS.isSupported();
        //#-
        return true;
        //#+
    }
}
