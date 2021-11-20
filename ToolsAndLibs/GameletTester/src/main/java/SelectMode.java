import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JDialog;

public class SelectMode extends JDialog
{

    private JPanel jContentPane = null;

    /**
     * This is the default constructor
     */
    public SelectMode()
    {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize()
    {
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane()
    {
        if (jContentPane == null)
        {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
        }
        return jContentPane;
    }

}
