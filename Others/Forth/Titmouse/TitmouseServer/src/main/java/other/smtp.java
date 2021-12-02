package other;

import com.igormaznitsa.R_HTTP.ScriptForthBuffer;

import java.io.*;
import java.net.*;

/**
 Sends a mail message via the SMTP protocol.
 **/

public class smtp
{
    private String SMTPServer = "";
    private String BackAddress = "titmouse@unknown.com";
    private int smtpPort = 25;
    private Socket socket;
    private PrintWriter ps;
    private BufferedReader dis;
    private boolean results = false;
    private String errorResults = "No Error";
    private String cr = "\r\n";

    /**
     Creates an illegal SMTP transaction.
     **/

    public smtp(String servername, int serverport, String backaddr)
    {
        BackAddress = backaddr;
        smtpPort = serverport;
        SMTPServer = servername;
    }

    /**
     Returns the results of the transmission
     **/

    public boolean result()
    {
        return results;
    }

    /**
     Returns the error results of the transmission
     **/

    public String errorResult()
    {
        return errorResults;
    }

    public boolean send(String toAddress, String smtpMessage)
    {
        send(SMTPServer, toAddress, BackAddress, smtpMessage);
        return this.result();
    }

    public boolean send(String toAddress, ScriptForthBuffer smtpMessage)
    {
        send(SMTPServer, toAddress, BackAddress, smtpMessage);
        return this.result();
    }

    /**
     Sends a message via SMTP on the specified port.
     @param smtpServer The server name
     @param toAddress  The address to send the message to
     @param fromAddress The address of the user sending the message
     @param smtpMessage The message to send
     **/

    void send(String smtpServer, String toAddress,
              String fromAddress, String smtpMessage)
    {
        InetAddress rina;
        InetAddress lina;
        results = true; // Assume success

        try
        {
// Open connection to SMTP server
            socket = new Socket(smtpServer, smtpPort);

// Send the form contents as a message
            rina = socket.getInetAddress();
            lina = rina.getLocalHost();
            ps = new PrintWriter(socket.getOutputStream());
            dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

// Send message

            if ((sendline("HELO " + lina.toString())) &&
                    (sendline("MAIL FROM:<" + fromAddress + ">")) &&
                    (sendline("RCPT TO:<" + toAddress + ">")) &&
                    (sendline("DATA")) &&
                    (sendline(smtpMessage)) &&
                    (sendline(".")) &&
                    (sendline("QUIT")))
            {
                results = true; // It is anyway, used for short-circuit
                errorResults = "Delivered to " + toAddress;
            }

            socket.close();
            socket = null;
        }
        catch (Exception ex)
        {
            results = false;
            errorResults = "Socket exception";

            try
            {
                socket.close();
                socket = null;
            }
            catch (Exception e)
            {
                errorResults = "Could not even close socket exception";
            }
        }
    }


    void send(String smtpServer, String toAddress,
              String fromAddress, ScriptForthBuffer smtpMessage)
    {
        InetAddress rina;
        InetAddress lina;
        results = true; // Assume success

        try
        {
// Open connection to SMTP server
            socket = new Socket(smtpServer, smtpPort);

// Send the form contents as a message
            rina = socket.getInetAddress();
            lina = rina.getLocalHost();
            ps = new PrintWriter(socket.getOutputStream());
            dis = new BufferedReader(new InputStreamReader(socket.getInputStream()));

// Send message

            if ((sendline("HELO " + lina.toString())) &&
                    (sendline("MAIL FROM:<" + fromAddress + ">")) &&
                    (sendline("RCPT TO:<" + toAddress + ">")) &&
                    (sendline("DATA")) &&
                    (sendline(smtpMessage)) &&
                    (sendline(".")) &&
                    (sendline("QUIT")))
            {
                results = true; // It is anyway, used for short-circuit
                errorResults = "Delivered to " + toAddress;
            }

            socket.close();
            socket = null;
        }
        catch (Exception ex)
        {
            results = false;
            errorResults = "Socket exception";

            try
            {
                socket.close();
                socket = null;
            }
            catch (Exception e)
            {
                errorResults = "Could not even close socket exception";
            }
        }
    }

    /**
     Send a line of data to the server, and retrieve the handshake
     */

    boolean sendline(String data) throws IOException
    {
        ps.print(data + cr);
        ps.flush();
        String s = dis.readLine();

        if (!((s.startsWith("2")) ||
                (s.startsWith("3"))))
        {
            results = false;
            errorResults = s;
        }

        return results;
    }

    boolean sendline(ScriptForthBuffer data) throws IOException
    {

        String ldata = new String(data.buffermem, 0, data.len);
        return sendline(ldata);
    }


    static String subnetDomain(String input)
    {
        String here = user(input);
        int pos = here.indexOf('%');

        if (pos == -1) // Not found
        {
            return domain(input);
        }

        return here.substring(pos + 1, here.length());
    }

    static String subnetUser(String input)
    {
        String here = user(input);
        int pos = here.indexOf('%');

        if (pos == -1) // Not found
        {
            return user(input);
            //pos = here.length();
        }

        return here.substring(0, pos);
    }


    static String domain(String input)
    {
        String here = wholeUser(input);
        int pos = here.indexOf('@');

        if ((pos == -1) || (pos >= (here.length() - 1)))
        {
            return "";
        }

        return here.substring(pos + 1, here.length());
    }

    static String user(String input)
    {
        String here = wholeUser(input);
        int pos = here.indexOf('@');

        if (pos == -1)
        {
            pos = here.length();
        }

        return here.substring(0, pos);
    }

    // Returns user name, without the <>s.

    static String wholeUser(String input)
    {
        int start = input.indexOf('<') + 1;
        int end = input.indexOf('>');

        if (end == -1) // Not found
        {
            end = input.length();
        }

        if ((input.length() == 0) || (start == end))
        {
            return "";
        }

        return input.substring(start, end);
    }
}

