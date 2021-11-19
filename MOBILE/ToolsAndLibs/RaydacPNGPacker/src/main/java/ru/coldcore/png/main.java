package ru.coldcore.png;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class main extends JFrame
{
    public static final String APPNAME = "PNG Packer";

    public static final String VERSION = "v.1.04b";

    private static final String HELPSTR = "Drop (GIF,JPG,TGA,PNG) image (or list) on the logo";

    public LogoConverter p_Converter;

    public main()
    {
        super(APPNAME + ' ' + VERSION);

        Container p_pane = getContentPane();

        p_pane.setLayout(new BorderLayout());
        p_pane.setBackground(Color.white);

        JLabel p_helpStr = new JLabel(HELPSTR);
        p_helpStr.setOpaque(false);

        p_pane.add(p_helpStr, BorderLayout.NORTH);

        JLabel p_destLabel = new JLabel(Utilities.loadIconFromResource("coldcorelogo.gif"));
        p_destLabel.setOpaque(false);

        p_pane.add(p_destLabel, BorderLayout.CENTER);

        setIconImage(Utilities.loadIconFromResource("icon.gif").getImage());

        p_Converter = new LogoConverter(this);
        p_Converter.setOpaque(false);

        setGlassPane(p_Converter);

        p_Converter.setVisible(true);

        JLabel p_copyright = new JLabel("©2002-2006 RRG Ltd. http://www.coldcore.ru");
        p_copyright.setOpaque(false);
        p_pane.add(p_copyright, BorderLayout.SOUTH);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String s_version = System.getProperty("java.version");
        if (s_version != null)
        {
            try
            {
            s_version = s_version.trim();
            if (s_version.length() >= 3)
            {
                //получаем версию системы как string формы:
                //[major].[minor].[release] (например 1.2.2)
                Double d_Version = new Double(s_version.substring(0, 3));

                //если используется JVM Microsoft,
                //используем обработчик потока от Microsoft
                if (s_version.indexOf("Microsoft") >= 0)
                {
                }
                else 
                if (1.5 <= d_Version.doubleValue())
                {
                    setAlwaysOnTop(true);
                }
            }
            }catch(Throwable _thr)
            {}
        }

        setResizable(false);
        Utilities.toScreenCenter(this);
        setVisible(true);
    }

    public static final void main(String[] _args)
    {
        if (_args.length == 0)
        {
            new main();
        }
        else
        {
            // работа с командной строкой
            System.out.println(APPNAME + '\n' + VERSION);
            System.out.println("©2002-2006 RRG Ltd. http://www.coldcore.ru");
            System.out.println();

            boolean lg_allGood = true;

            for (int li = 0; li < _args.length; li++)
            {
                String s_file = _args[li];
                File p_file = new File(s_file);
                if (!p_file.exists())
                {
                    System.err.println("ERROR: File " + s_file + " can not be found");
                    lg_allGood = false;
                }
                else
                {
                    try
                    {
                        LogoConverter.convertFile(p_file, null);
                        System.out.println("File " + s_file + " has been packed successfully");
                    }
                    catch (Throwable _thr)
                    {
                        System.err.println("ERROR: File " + s_file + " [" + _thr.getMessage() + "]");
                        lg_allGood = false;
                    }
                }
            }

            if (lg_allGood)
                System.exit(0);
            else
                System.exit(1);
        }
    }
}
